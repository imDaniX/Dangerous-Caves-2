/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.caves.mobs;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.papermc.lib.PaperLib;
import me.imdanix.caves.Manager;
import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.ticks.Dynamics;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.ticks.Tickable;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import me.imdanix.caves.util.random.WeightedPool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages custom mob spawning and registering
 */
public class MobsManager implements Manager<CustomMob>, Listener, Tickable, Configurable {
    private static final Multimap<CustomMob.Ticking, UUID> tickingEntities = HashMultimap.create();

    private boolean disabled;

    private final MetadataValue MARKER;

    private final Plugin plugin;
    private final Configuration config;
    private final Dynamics dynamics;

    private final Map<String, CustomMob> mobs;
    private final Set<String> worlds;
    private WeightedPool<CustomMob> mobsPool;

    private Set<EntityType> replaceTypes;
    private int yMin;
    private int yMax;
    private double chance;
    private boolean blockRename;
    private boolean metadata;

    private Listener spawnListener;

    public MobsManager(Plugin plugin, Configuration config, Dynamics dynamics) {
        MARKER = new FixedMetadataValue(plugin, new Object());
        this.plugin = plugin;
        this.config = config;
        this.dynamics = dynamics;
        mobs = new HashMap<>();
        worlds = new HashSet<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("try-chance", 50) / 100;
        blockRename = cfg.getBoolean("restrict-rename", false);
        yMin = cfg.getInt("y-min", 0);
        yMax = cfg.getInt("y-max", 64);

        worlds.clear();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);
        replaceTypes = Utils.getEnumSet(EntityType.class, cfg.getStringList("replace-mobs"));

        disabled = !(cfg.getBoolean("enabled", true) && chance > 0 && yMin > 0 && yMax > 0 &&
                !worlds.isEmpty() && !replaceTypes.isEmpty());
        recalculate();

        metadata = cfg.getBoolean("add-metadata", false);

        if(cfg.getBoolean("use-prespawn", true) && PaperLib.isPaper()) {
            if(spawnListener == null) {
                spawnListener = new PaperSpawnListener();
                Bukkit.getPluginManager().registerEvents(spawnListener, plugin);
            } else if(!(spawnListener instanceof PaperSpawnListener)) {
                HandlerList.unregisterAll(spawnListener);
                spawnListener = new PaperSpawnListener();
                Bukkit.getPluginManager().registerEvents(spawnListener, plugin);
            }
        } else {
            if(spawnListener == null) {
                spawnListener = new SpigotSpawnListener();
                Bukkit.getPluginManager().registerEvents(spawnListener, plugin);
            } else if(!(spawnListener instanceof SpigotSpawnListener)) {
                HandlerList.unregisterAll(spawnListener);
                spawnListener = new SpigotSpawnListener();
                Bukkit.getPluginManager().registerEvents(spawnListener, plugin);
            }
        }
    }

    /**
     * Recalculate mobs spawn chances
     */
    private void recalculate() {
        Set<CustomMob> mobsSet = new HashSet<>();
        mobs.values().forEach(m -> {if(m.getWeight() > 0) mobsSet.add(m);});
        if(mobsSet.isEmpty())
            disabled = true;
        else
            mobsPool = new WeightedPool<>(mobsSet, CustomMob::getWeight);
    }

    /**
     * Register a new custom mob
     * @param mob Mob to register
     */
    @Override
    public boolean register(CustomMob mob) {
        if(!mobs.containsKey(mob.getCustomType())) {
            mobsPool.add(mob, mob.getWeight());
            Compatibility.cacheTag(mob.getCustomType());
            mobs.put(mob.getCustomType(), mob);
            if(mob instanceof Configurable)
                config.register((Configurable) mob);
            if(mob instanceof Listener)
                Bukkit.getPluginManager().registerEvents((Listener) mob, plugin);
            if(mob instanceof Tickable)
                dynamics.register((Tickable) mob);
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        Iterator<Map.Entry<CustomMob.Ticking, UUID>> iter = tickingEntities.entries().iterator();
        while(iter.hasNext()) {
            Map.Entry<CustomMob.Ticking, UUID> mob = iter.next();
            Entity entity = Bukkit.getEntity(mob.getValue());
            if(entity == null) {
                iter.remove();
            } else mob.getKey().tick((LivingEntity) entity);
        }
    }

    @Override
    public TickLevel getTickLevel() {
        return TickLevel.ENTITY;
    }

    public CustomMob getMob(String type) {
        return mobs.get(type);
    }

    /**
     * Try to summon a new mob
     * @param type Type of mob to summon
     * @param loc Where to summon
     * @return Is summoning was successful
     */
    public LivingEntity spawn(String type, Location loc) {
        CustomMob mob = mobs.get(type);
        if(mob == null) return null;
        return spawn(mob, loc);
    }

    /**
     * Try to summon a new mob
     * @param mob Type of mob to summon
     * @param loc Where to summon
     * @return Is summoning was successful
     */
    public LivingEntity spawn(CustomMob mob, Location loc) {
        LivingEntity entity = mob.spawn(loc);
        Compatibility.setTag(entity, mob.getCustomType());
        if(metadata) entity.setMetadata("DangerousCaves", MARKER);
        entity.setRemoveWhenFarAway(true);
        return entity;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(!event.getChunk().isLoaded()) return;
            for(Entity entity : event.getChunk().getEntities()) {
                if(!(entity instanceof LivingEntity)) continue;
                LivingEntity livingEntity = (LivingEntity) entity;
                String type = Compatibility.getTag(livingEntity);
                if(type != null) {
                    if(metadata) entity.setMetadata("DangerousCaves", MARKER);
                    CustomMob mob = mobs.get(type);
                    if(mob instanceof CustomMob.Ticking)
                        handle(livingEntity, (CustomMob.Ticking) mob);
                }
            }
        }, 1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Material item = event.getHand() == EquipmentSlot.HAND ?
                event.getPlayer().getInventory().getItemInMainHand().getType() :
                event.getPlayer().getInventory().getItemInOffHand().getType();
        if(blockRename && item == Material.NAME_TAG &&
                entity instanceof LivingEntity && Compatibility.isTagged((LivingEntity)entity)) {
            event.setCancelled(true);
        }
    }

    private boolean onSpawn(EntityType type, Location loc, CreatureSpawnEvent.SpawnReason reason) {
        if(disabled || reason != CreatureSpawnEvent.SpawnReason.NATURAL ||
                !replaceTypes.contains(type) ||
                loc.getBlockY() > yMax || loc.getBlockY() < yMin ||
                !worlds.contains(loc.getWorld().getName()) ||
                !Locations.isCave(loc) ||
                !Rnd.chance(chance))
            return false;
        CustomMob mob = mobsPool.next();
        if(mob.canSpawn(loc)) {
            spawn(mob, loc);
            return true;
        }
        return false;
    }

    @Override
    public String getPath() {
        return "mobs";
    }

    private class PaperSpawnListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onSpawn(PreCreatureSpawnEvent event) {
            if(MobsManager.this.onSpawn(event.getType(), event.getSpawnLocation(), event.getReason()))
                event.setCancelled(true);
        }
    }

    private class SpigotSpawnListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onSpawn(CreatureSpawnEvent event) {
            if(MobsManager.this.onSpawn(event.getEntityType(), event.getLocation(), event.getSpawnReason()))
                event.setCancelled(true);
        }
    }

    /**
     * Handle a new ticking mob
     * @param entity Entity to handle
     * @param mob Related custom mob
     */
    public static void handle(LivingEntity entity, CustomMob.Ticking mob) {
        tickingEntities.put(mob, entity.getUniqueId());
    }
}
