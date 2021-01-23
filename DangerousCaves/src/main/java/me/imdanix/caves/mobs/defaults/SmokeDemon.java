package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.mobs.TickingMob;
import me.imdanix.caves.util.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SmokeDemon extends TickingMob {
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 120, 1);
    private static final PotionEffect WITHER = new PotionEffect(PotionEffectType.WITHER, 120, 0);
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);

    private String name;
    private double health;
    private int maxLight;

    private double radius;

    public SmokeDemon() {
        super(EntityType.ZOMBIE, "smoke-demon", 7);
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        name = Utils.clr(cfg.getString("name", "&4Smoke Demon"));
        health = cfg.getDouble("health", 20);
        maxLight = cfg.getInt("max-light", 11);
        radius = cfg.getInt("harm-radius", 3);
    }

    @Override
    public boolean canSpawn(Location loc) {
        return loc.getBlock().getLightLevel() <= maxLight;
    }

    @Override
    public void setup(LivingEntity entity) {
        if (!name.isEmpty()) entity.setCustomName(name);
        Utils.setMaxHealth(entity, health);

        entity.setCustomNameVisible(false);
        entity.addPotionEffect(INVISIBILITY);
        entity.setSilent(true);
    }

    @Override
    public void tick(LivingEntity entity) {
        if (entity.getLocation().getBlock().getLightLevel() > maxLight) {
            entity.remove();
            return;
        }
        entity.getNearbyEntities(radius, radius, radius).forEach(this::harm);
        entity.getWorld().spawnParticle(Particle.CLOUD, entity.getLocation().add(0, 1, 0), 30, 1, 1, 1, 0f);
    }

    private void harm(Entity entity) {
        if (entity instanceof LivingEntity && entity.getLocation().getBlock().getLightLevel() <= maxLight) {
            LivingEntity living = (LivingEntity) entity;
            living.addPotionEffect(BLINDNESS);
            living.addPotionEffect(WITHER);
        }
    }
}
