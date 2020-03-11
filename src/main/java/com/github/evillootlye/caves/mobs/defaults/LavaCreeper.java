package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.TickableMob;
import com.github.evillootlye.caves.util.Locations;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Configurable.Path("mobs.lava-creeper")
public class LavaCreeper extends TickableMob implements Configurable, Listener {
    private static final PotionEffect FIRE_RESISTANCE = new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
            Integer.MAX_VALUE, 1, false, false);
    private int weight;
    private double chance;
    private double fire, magmaBlock, obsidian, lava;
    private int fireTouch;
    private int radius, radiusSquared;

    public LavaCreeper() {
        super(EntityType.CREEPER, "lava-creeper");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
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
        if(fireTouch <= 0 || !isThis(event.getEntity())) return;
        event.getDamager().setFireTicks(fireTouch);
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        if(!isThis(event.getEntity()) || chance <= 0) return;
        Location start = event.getLocation();
        int cx = start.getBlockX();
        int cy = start.getBlockY();
        int cz = start.getBlockZ();
        Locations.loop(radius, start, (world, x, y, z) -> {
            if(((cx - x)^2 + (cy - y)^2 + (cz - z)^2) > radiusSquared || chance < Rnd.nextDouble()) return;
            Block block = new Location(world, x, y, z).getBlock();
            if(block.getType() == Material.AIR) {
                if(fire > 0 && Rnd.nextDouble() < fire) block.setType(Material.FIRE);
            } else if(block.getType() != Material.BEDROCK) {
                if(magmaBlock > 0 && Rnd.nextDouble() < magmaBlock) {
                    block.setType(Material.MAGMA_BLOCK);
                } else if(obsidian > 0 && Rnd.nextDouble() < obsidian) {
                    block.setType(Material.OBSIDIAN);
                } else if(lava > 0 && Rnd.nextDouble() < lava) {
                    block.setType(Material.LAVA);
                }
            }
        });
    }

    @Override
    public void tick(LivingEntity entity) {
        entity.getWorld().spawnParticle(Particle.LAVA, entity.getLocation().add(0, 1, 0), 2, 0.4, 0.9, 0.4);
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
