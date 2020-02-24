package com.github.evillootlye.caves.mobs;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.utils.LocationUtils;
import com.github.evillootlye.caves.utils.Rnd;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Configurable.Path("mobs.alpha-spider")
public class AlphaSpider extends CustomMob implements Listener {
    private static final PotionEffect POISON = new PotionEffect(PotionEffectType.POISON, 75, 1);

    public AlphaSpider() {
        super(EntityType.SPIDER, "alpha-spider");
    }

    @Override
    public void setup(LivingEntity entity) {
        // entity.setCustomName(name);
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

                LocationUtils.loopRelative(3, 3, 3, loc, l -> {
                    if (Rnd.nextDouble() < 0.03 && l.getBlock().getType() == Material.AIR) {
                        l.getBlock().setType(Material.COBWEB);
                    }}
                );
            }
            entity.addPotionEffect(POISON);
        }
    }
}
