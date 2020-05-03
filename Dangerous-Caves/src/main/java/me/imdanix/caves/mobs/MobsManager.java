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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.imdanix.caves.DangerousCaves;
import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.ticks.Tickable;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import me.imdanix.caves.util.random.WeightedPool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MobsManager implements Listener, Tickable, Configurable {
    private static final Multimap<TickingMob, UUID> tickingEntities = ArrayListMultimap.create();

    private final MetadataValue marker;

    private final DangerousCaves plugin;
    private final Map<String, CustomMob> mobs;
    private final Set<String> worlds;
    private WeightedPool<CustomMob> mobsPool;

    private Set<EntityType> replaceTypes;
    private int yMin;
    private int yMax;
    private double chance;
    private boolean blockRename;

    public MobsManager(DangerousCaves plugin) {
        marker = new FixedMetadataValue(plugin, new Object());
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            Bukkit.getPluginManager().registerEvents(new PaperEventListener(), plugin);
        } catch (ClassNotFoundException e) {
            Bukkit.getPluginManager().registerEvents(new SpigotEventListener(), plugin);
        }
        this.plugin = plugin;
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
        if(chance > 0) recalculate();
    }

    public void recalculate() {
        Set<CustomMob> mobsSet = new HashSet<>();
        mobs.values().forEach(m -> {if(m.getWeight() > 0) mobsSet.add(m);});
        if(mobsSet.isEmpty())
            chance = 0;
        else
            mobsPool = new WeightedPool<>(mobsSet, CustomMob::getWeight);
    }

    public void register(CustomMob mob) {
        if(!mobs.containsKey(mob.getCustomType())) {
            Compatibility.cacheTag(mob.getCustomType());
            plugin.getConfiguration().register(mob);
            mobs.put(mob.getCustomType(), mob);
            if(mob instanceof Listener)
                Bukkit.getPluginManager().registerEvents((Listener)mob, plugin);
            if(mob instanceof Tickable)
                plugin.getDynamics().subscribe((Tickable)mob);
        }
    }

    @Override
    public void tick() {
        Iterator<Map.Entry<TickingMob, UUID>> iter = tickingEntities.entries().iterator();
        while(iter.hasNext()) {
            Map.Entry<TickingMob, UUID> mob = iter.next();
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

    public boolean summon(String type, Location loc) {
        CustomMob mob = mobs.get(type.toLowerCase());
        if(mob == null) return false;
        spawn(mob, loc);
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for(Entity entity : event.getChunk().getEntities()) {
                if(!(entity instanceof LivingEntity)) continue;
                LivingEntity livingEntity = (LivingEntity) entity;
                String type = Compatibility.getTag(livingEntity);
                if(type != null) {
                    entity.setMetadata("DangerousCaves", marker);
                    CustomMob mob = mobs.get(type);
                    if(mob instanceof TickingMob)
                        handle(livingEntity, (TickingMob) mob);
                }
            }
        }, 1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if(blockRename && entity instanceof LivingEntity &&
                Compatibility.getTag((LivingEntity)entity) != null) {
            event.setCancelled(true);
            ((LivingEntity)entity).setRemoveWhenFarAway(false);
        }
    }

    private boolean onSpawn(EntityType type, Location loc, CreatureSpawnEvent.SpawnReason reason) {
        if(chance <= 0 || reason != CreatureSpawnEvent.SpawnReason.NATURAL ||
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

    private LivingEntity spawn(CustomMob mob, Location loc) {
        LivingEntity entity = mob.spawn(loc);
        entity.setMetadata("DangerousCaves", marker);
        entity.setRemoveWhenFarAway(true);
        return entity;
    }

    @Override
    public String getPath() {
        return "mobs";
    }

    private class PaperEventListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onSpawn(PreCreatureSpawnEvent event) {
            if(MobsManager.this.onSpawn(event.getType(), event.getSpawnLocation(), event.getReason()))
                event.setCancelled(true);
        }
    }

    private class SpigotEventListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onSpawn(CreatureSpawnEvent event) {
            if(MobsManager.this.onSpawn(event.getEntityType(), event.getLocation(), event.getSpawnReason()))
                event.setCancelled(true);
        }
    }

    public static int handledCount() {
        return tickingEntities.size();
    }

    public static void handle(LivingEntity entity, TickingMob mob) {
        tickingEntities.put(mob, entity.getUniqueId());
    }
}
