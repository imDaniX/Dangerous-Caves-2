package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.mobs.MobBase;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.random.Rng;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AlphaSpider extends MobBase implements Listener {
    private static final PotionEffect POISON = new PotionEffect(PotionEffectType.POISON, 75, 1);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false, true);

    private double cobwebChance;
    private double minionChance;

    public AlphaSpider() {
        super(EntityType.SPIDER, "alpha-spider", 9, 18d);
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        cobwebChance = cfg.getDouble("cobweb-chance", 14.29) / 100;
        minionChance = cfg.getDouble("minion-chance", 6.67) / 100;
    }

    @Override
    public void setup(LivingEntity entity) {
        entity.addPotionEffect(REGENERATION);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!isThis(event.getDamager())) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        Entity damager = event.getDamager();
        if (Rng.nextBoolean()) {
            if (minionChance > 0 && Rng.chance(minionChance))
                damager.getWorld().spawnEntity(damager.getLocation(), EntityType.CAVE_SPIDER);

            if (cobwebChance > 0) {
                Location loc = event.getEntity().getLocation();
                loc.getBlock().setType(Material.COBWEB);
                entity.getEyeLocation().getBlock().setType(Material.COBWEB);

                Locations.loop(3, loc, l -> {
                    if (Materials.isAir(l.getBlock().getType()) && Rng.chance(cobwebChance) &&
                            Regions.INSTANCE.check(CheckType.ENTITY, l)) {
                        l.getBlock().setType(Material.COBWEB);
                    }}
                );
            }
        } else entity.addPotionEffect(POISON);
    }
}
