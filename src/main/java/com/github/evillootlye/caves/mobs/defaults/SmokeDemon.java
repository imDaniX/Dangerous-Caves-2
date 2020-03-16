package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.TickableMob;
import com.github.evillootlye.caves.util.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SmokeDemon extends TickableMob implements Configurable {
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 120, 1);
    private static final PotionEffect WITHER = new PotionEffect(PotionEffectType.WITHER, 120, 0);
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);

    private int weight;
    private String name;
    private double radius;

    public SmokeDemon() {
        super(EntityType.ZOMBIE, "smoke-demon");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", ""));
        radius = cfg.getInt("harm-radius", 3);
    }

    @Override
    public boolean canSpawn(EntityType type, Location loc) {
        return loc.getBlock().getLightLevel() < 12;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        entity.addPotionEffect(INVISIBILITY);
        entity.setSilent(true);
    }

    @Override
    public void tick(LivingEntity entity) {
        if(entity.getLocation().getBlock().getLightLevel() >= 12) {
            entity.remove();
            return;
        }
        entity.getNearbyEntities(radius, radius, radius).forEach(SmokeDemon::harm);
        entity.getWorld().spawnParticle(Particle.CLOUD, entity.getLocation().add(0, 1, 0), 20, 1, 1, 1, 0f);
    }

    private static void harm(Entity entity) {
        if(entity instanceof LivingEntity && entity.getLocation().getBlock().getLightLevel() < 12) {
            LivingEntity living = (LivingEntity) entity;
            living.addPotionEffect(BLINDNESS);
            living.addPotionEffect(WITHER);
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
