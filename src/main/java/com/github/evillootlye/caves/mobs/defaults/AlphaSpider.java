package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.CustomMob;
import com.github.evillootlye.caves.util.Locations;
import com.github.evillootlye.caves.util.Materials;
import com.github.evillootlye.caves.util.random.Rnd;
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

@Configurable.Path("mobs.alpha-spider")
public class AlphaSpider extends CustomMob implements Listener, Configurable {
    private static final PotionEffect POISON = new PotionEffect(PotionEffectType.POISON, 75, 1);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0);

    private int weight;

    public AlphaSpider() {
        super(EntityType.SPIDER, "alpha-spider");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
    }

    @Override
    public void setup(LivingEntity entity) {
        entity.addPotionEffect(REGENERATION);
        Entity caveSpider = entity.getWorld().spawnEntity(entity.getLocation(), EntityType.CAVE_SPIDER);
        entity.addPassenger(caveSpider);
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!isThis(event.getDamager())) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        Entity damager = event.getDamager();
        if (Rnd.nextBoolean()) {
            if (Rnd.nextDouble() < 0.2) {
                Location loc = event.getEntity().getLocation();
                loc.getBlock().setType(Material.COBWEB);
                entity.getEyeLocation().getBlock().setType(Material.COBWEB);

                if (Rnd.nextDouble() < 0.07)
                    damager.getWorld().spawnEntity(damager.getLocation(), EntityType.CAVE_SPIDER);

                Locations.loop(3, loc, l -> {
                    if (Rnd.nextDouble() < 0.03 && Materials.isAir(l.getBlock().getType())) {
                        l.getBlock().setType(Material.COBWEB);
                    }}
                );
            }
            entity.addPotionEffect(POISON);
        }
    }
}
