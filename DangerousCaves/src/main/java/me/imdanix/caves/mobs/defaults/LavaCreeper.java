package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.mobs.CustomMob;
import me.imdanix.caves.mobs.MobBase;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.random.Rng;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LavaCreeper extends MobBase implements CustomMob.Ticking, Listener {
    private static final PotionEffect FIRE_RESISTANCE = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false);

    private double chance;
    private double fire;
    private double magmaBlock;
    private double obsidian;
    private double lava;

    private int fireTouch;
    private int radius;
    private int radiusSquared;

    public LavaCreeper() {
        super(EntityType.CREEPER, "lava-creeper", 6);
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        chance = cfg.getDouble("change-chance", 50) / 100;
        fire = cfg.getDouble("blocks.fire", 33.33) / 100;
        magmaBlock = cfg.getDouble("blocks.magma_block", 25) / 100;
        obsidian = cfg.getDouble("blocks.obsidian", 20) / 100;
        lava = cfg.getDouble("blocks.lava", 16.67) / 100;
        fireTouch = cfg.getInt("fire-touch", 10);
        radius = cfg.getInt("radius", 4);
        radiusSquared = radius * radius;
    }

    @Override
    public void setup(LivingEntity entity) {
        entity.addPotionEffect(FIRE_RESISTANCE);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (fireTouch <= 0 || !isThis(event.getEntity())) return;
        event.getDamager().setFireTicks(fireTouch);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        if (!isThis(event.getEntity()) || chance <= 0) return;
        ((LivingEntity)event.getEntity()).removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        Location start = event.getLocation();
        int cx = start.getBlockX();
        int cy = start.getBlockY();
        int cz = start.getBlockZ();
        start.getWorld().spawnParticle(Particle.FLAME, cx, cy+1, cz, 20, 0, 0, 0, 2);
        Locations.loop(radius, start, (world, x, y, z) -> {
            if ((square(cx - x) + square(cy - y) + square(cz - z)) > radiusSquared || !Rng.chance(chance)) return;
            Block block = new Location(world, x, y, z).getBlock();
            if (block.getType().isAir()) {
                if (fire > 0 && !block.getRelative(BlockFace.DOWN).getType().isAir() && Rng.chance(fire)) block.setType(Material.FIRE);
            } else if (block.getType() != Material.BEDROCK && Regions.INSTANCE.check(CheckType.ENTITY, block.getLocation())) {
                if (magmaBlock > 0 && Rng.chance(magmaBlock)) {
                    block.setType(Material.MAGMA_BLOCK);
                } else if (obsidian > 0 && Rng.chance(obsidian)) {
                    block.setType(Material.OBSIDIAN);
                } else if (lava > 0 && Rng.chance(lava)) {
                    block.setType(Material.LAVA);
                }
            }
        });
    }

    @Override
    public void tick(LivingEntity entity) {
        entity.getWorld().spawnParticle(Particle.LAVA, entity.getLocation().add(0, 1, 0), 1, 0.3, 0.8, 0.3);
    }

    private static int square(int i) {
        return i*i;
    }
}
