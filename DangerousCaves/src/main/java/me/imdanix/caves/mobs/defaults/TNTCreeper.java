package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.mobs.MobBase;
import me.imdanix.caves.util.random.Rng;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class TNTCreeper extends MobBase implements Listener {
    private static final PotionEffect INCREASE_DAMAGE = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, true);

    private int tntAmount;
    private double explosionChance;

    public TNTCreeper() {
        super(EntityType.CREEPER, "tnt-creeper", 9, null, ChatColor.DARK_RED + "TNT Creeper");
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        tntAmount = cfg.getInt("tnt-amount", 2);
        explosionChance = cfg.getDouble("explosion-chance", 33.33) / 100;
    }

    @Override
    public void prepare(LivingEntity entity) {
        entity.addPotionEffect(INCREASE_DAMAGE);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        if (!isThis(event.getEntity())) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        entity.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        Location loc = event.getLocation();
        for (int i = 0; i < tntAmount; i++) {
            Entity tnt = entity.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
            tnt.setVelocity(new Vector(Rng.nextDouble(2) - 1, 0.3, Rng.nextDouble(2) - 1));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (explosionChance > 0 && isThis(event.getEntity()) && Rng.chance(explosionChance)) {
            event.getDamager().getWorld().createExplosion(event.getDamager().getLocation(), 0.01f);
        }
    }
}
