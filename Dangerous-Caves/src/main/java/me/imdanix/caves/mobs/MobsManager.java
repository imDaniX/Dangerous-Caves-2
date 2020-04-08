package me.imdanix.caves.mobs;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import me.imdanix.caves.DangerousCaves;
import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.ticks.Tickable;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.AliasMethod;
import me.imdanix.caves.util.random.Rnd;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@Configurable.Path("mobs")
public class MobsManager implements Listener, Tickable, Configurable {

    private static final Map<LivingEntity, TickableMob> tickingEntities = new WeakHashMap<>();

    private final DangerousCaves plugin;
    private final Map<String, CustomMob> mobs;
    private final Set<String> worlds;
    private AliasMethod<CustomMob> mobsPool;

    private int yMin;
    private int yMax;
    private double chance;
    private boolean blockRename;

    public MobsManager(DangerousCaves plugin) {
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
        if(chance > 0) recalculate();
    }

    public void recalculate() {
        Set<CustomMob> mobsSet = new HashSet<>();
        mobs.values().forEach(m -> {if(m.getWeight() > 0) mobsSet.add(m);});
        if(mobsSet.isEmpty())
            chance = 0;
        else
            mobsPool = new AliasMethod<>(mobsSet, CustomMob::getWeight);
    }

    public void register(CustomMob mob) {
        if(!mobs.containsKey(mob.getCustomType())) {
            Compatibility.cacheTag(mob.getCustomType());
            mobs.put(mob.getCustomType(), mob);
            if(mob instanceof Listener)
                Bukkit.getPluginManager().registerEvents((Listener)mob, plugin);
            if(mob instanceof Configurable)
                plugin.getConfiguration().register((Configurable)mob);
            if(mob instanceof Tickable)
                plugin.getDynamics().subscribe((Tickable)mob);
        }
    }

    @Override
    public void tick() {
        Set<LivingEntity> dead = new HashSet<>();
        for(Map.Entry<LivingEntity, TickableMob> mob : tickingEntities.entrySet()) {
            LivingEntity entity = mob.getKey();
            if(entity.isDead()) {
                dead.add(entity);
                continue;
            }
            mob.getValue().tick(entity);
        }
        dead.forEach(tickingEntities::remove);
    }

    @Override
    public TickLevel getTickLevel() {
        return TickLevel.ENTITY;
    }

    public boolean summon(String type, Location loc) {
        CustomMob mob = mobs.get(type.toLowerCase());
        if(mob == null) return false;
        mob.spawn(loc);
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
                    CustomMob mob = mobs.get(type);
                    if(mob instanceof TickableMob)
                        handle(livingEntity, (TickableMob) mob);
                }
            }
        }, 1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEntityEvent event) {
        if(blockRename && event.getRightClicked() instanceof LivingEntity &&
                Compatibility.getTag((LivingEntity)event.getRightClicked()) != null)
            event.setCancelled(true);
    }

    private boolean onSpawn(EntityType type, Location loc, CreatureSpawnEvent.SpawnReason reason) {
        if(chance <= 0 || reason != CreatureSpawnEvent.SpawnReason.NATURAL ||
                loc.getBlockY() > yMax || loc.getBlockY() < yMin ||
                !worlds.contains(loc.getWorld().getName()) ||
                !Locations.isCave(loc) ||
                !Rnd.chance(chance))
            return false;
        CustomMob mob = mobsPool.next();
        if(mob.canSpawn(type, loc)) {
            mob.spawn(loc);
            return true;
        }
        return false;
    }

    private class PaperEventListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onSpawn(PreCreatureSpawnEvent event) {
            if(MobsManager.this.onSpawn(event.getType(), event.getSpawnLocation(), event.getReason()))
                event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onRemove(EntityRemoveFromWorldEvent event) {
            Entity entity = event.getEntity();
            if(entity instanceof LivingEntity)
                unhandle((LivingEntity) entity);
        }
    }

    private class SpigotEventListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onSpawn(CreatureSpawnEvent event) {
            if(MobsManager.this.onSpawn(event.getEntityType(), event.getLocation(), event.getSpawnReason()))
                event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onDeath(EntityDeathEvent event) {
            unhandle(event.getEntity());
        }
    }

    public static int handledCount() {
        return tickingEntities.size();
    }

    public static void unhandle(LivingEntity entity) {
        tickingEntities.remove(entity);
    }

    public static void handle(LivingEntity entity, TickableMob mob) {
        tickingEntities.put(entity, mob);
    }
}
