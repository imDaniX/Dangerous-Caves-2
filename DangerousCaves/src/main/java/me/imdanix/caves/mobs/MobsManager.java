package me.imdanix.caves.mobs;

import me.imdanix.caves.util.TagHelper;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.ticks.Dynamics;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.ticks.Tickable;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Manager;
import me.imdanix.caves.util.PlayerAttackedEvent;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rng;
import me.imdanix.caves.util.random.WeightedPool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Manages custom mob spawning and registering
 */
// TODO: Split into Manager and Registry
public class MobsManager implements Manager<CustomMob>, Listener, Tickable, Configurable {
    private static final Pattern CUSTOM_TYPE_PATTERN = Pattern.compile("[a-z\\d_-]+");

    private final Map<CustomMob.Ticking, Set<UUID>> tickingEntities = new HashMap<>();
    private final MetadataValue MARKER;

    private final Plugin plugin;
    private final Configuration config;
    private final Dynamics dynamics;

    private final Map<String, CustomMob> mobs;
    private final Set<String> worlds;
    private boolean disabled;
    private WeightedPool<CustomMob> mobsPool;

    private Set<EntityType> replaceTypes;
    private int yMax;
    private double chance;
    private boolean blockRename;
    private boolean metadata;
    private Predicate<Location> lightCheck;

    public MobsManager(Plugin plugin, Configuration config, Dynamics dynamics) {
        MARKER = new FixedMetadataValue(plugin, new Object());
        this.plugin = plugin;
        this.config = config;
        this.dynamics = dynamics;
        mobs = new HashMap<>();
        worlds = new HashSet<>();
        mobsPool = new WeightedPool<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("try-chance", 50) / 100;
        blockRename = cfg.getBoolean("restrict-rename", false);
        yMax = cfg.getInt("y-max", 64);

        int maxLight = cfg.getInt("max-light-level", 16);
        if (maxLight < 16) {
            lightCheck = (l) -> l.getBlock().getLightLevel() <= Math.max(0, maxLight);
        } else {
            lightCheck = (l) -> true;
        }

        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);

        List<String> replaceMobs = cfg.isList("replace-mobs") ?
                                   cfg.getStringList("replace-mobs") :
                                   Arrays.asList("ZOMBIE", "HUSK", "SKELETON", "STRAY", "CREEPER",
                                                                                        "SPIDER", "WITCH", "ENDERMAN");
        replaceTypes = Utils.getEnumSet(EntityType.class, replaceMobs);

        disabled = !cfg.getBoolean("enabled", true);
        recalculateChances();

        metadata = cfg.getBoolean("add-metadata", false);
        CreatureSpawnEvent.getHandlerList().unregister(this);
        if (!disabled) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
    }

    /**
     * Recalculate mobs spawn chances
     */
    private void recalculateChances() {
        mobsPool = new WeightedPool<>();
        mobs.values().forEach(m -> mobsPool.add(m, m.getWeight()));
        if (mobsPool.isEmpty()) {
            disabled = true;
        }
    }

    /**
     * Register a new custom mob
     * @param mob Mob to register
     */
    @Override
    public boolean register(CustomMob mob) {
        if (!CUSTOM_TYPE_PATTERN.matcher(mob.getCustomType()).matches() || mobs.containsKey(mob.getCustomType())) {
            return false;
        }
        mobsPool.add(mob, mob.getWeight());
        mobs.put(mob.getCustomType(), mob);
        if (mob instanceof Configurable configurable)
            config.register(configurable);
        if (mob instanceof Listener listener)
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        if (mob instanceof Tickable tickable)
            dynamics.register(tickable);
        if (mob instanceof CustomMob.Ticking ticking)
            tickingEntities.put(ticking, new HashSet<>());
        return true;
    }

    @Override
    public void tick() {
        for (Map.Entry<CustomMob.Ticking, Set<UUID>> entry : tickingEntities.entrySet()) {
            CustomMob.Ticking mob = entry.getKey();
            Iterator<UUID> uuids = entry.getValue().iterator();
            while (uuids.hasNext()) {
                Entity entity = Bukkit.getEntity(uuids.next());
                if (entity == null) {
                    uuids.remove();
                } else mob.tick((LivingEntity) entity);
            }
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
        if (mob == null) return null;
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
        entity.addScoreboardTag(TagHelper.SCOREBOARD_TAG);
        TagHelper.setTag(entity, mob.getCustomType());
        if (metadata) entity.setMetadata("DangerousCaves", MARKER);
        if (mob instanceof CustomMob.Ticking ticking) {
            handleTicking(entity, ticking);
        }
        entity.setRemoveWhenFarAway(true);
        return entity;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;
            String type = TagHelper.getTag(livingEntity);
            if (type != null) {
                if (metadata) entity.setMetadata("DangerousCaves", MARKER);
                CustomMob mob = mobs.get(type);
                if (mob instanceof CustomMob.Ticking ticking) {
                    handleTicking(livingEntity, ticking);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && event.getDamager() instanceof LivingEntity) {
            PlayerAttackedEvent pEvent = new PlayerAttackedEvent((Player) event.getEntity(), (LivingEntity) event.getDamager(), event.getDamage());
            Bukkit.getPluginManager().callEvent(pEvent);
            event.setDamage(pEvent.getDamage());
            event.setCancelled(pEvent.isCancelled());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!blockRename) return;
        Material item = event.getHand() == EquipmentSlot.HAND ?
                event.getPlayer().getInventory().getItemInMainHand().getType() :
                event.getPlayer().getInventory().getItemInOffHand().getType();
        if (item == Material.NAME_TAG && event.getRightClicked() instanceof LivingEntity living
            && TagHelper.isTagged(living)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        if (onSpawn(event.getEntityType(), event.getLocation(), event.getSpawnReason()))
            event.setCancelled(true);
    }

    private boolean onSpawn(EntityType type, Location loc, CreatureSpawnEvent.SpawnReason reason) {
        if (disabled || reason != CreatureSpawnEvent.SpawnReason.NATURAL ||
                !replaceTypes.contains(type) ||
                !lightCheck.test(loc) ||
                loc.getBlockY() > yMax ||
                !worlds.contains(loc.getWorld().getName()) ||
                !Locations.isCave(loc) ||
                !Rng.chance(chance))
            return false;
        CustomMob mob = mobsPool.next();
        if (mob.canSpawn(loc)) {
            spawn(mob, loc);
            return true;
        }
        return false;
    }

    public boolean checkWorld(String world) {
        return worlds.contains(world);
    }

    @Override
    public String getConfigPath() {
        return "mobs";
    }

    /**
     * Handle a new ticking mob
     * @param entity Entity to handle
     * @param mob Related custom mob
     */
    public void handleTicking(LivingEntity entity, CustomMob.Ticking mob) {
        tickingEntities.get(mob).add(entity.getUniqueId());
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Set<String> getMobs() {
        return mobs.keySet();
    }
}
