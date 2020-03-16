package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.CustomMob;
import com.github.evillootlye.caves.util.Utils;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@Configurable.Path("mobs.tnt-creeper")
public class TNTCreeper extends CustomMob implements Configurable, Listener {
    private static final PotionEffect INCREASE_DAMAGE = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, true);
    private int weight;
    private String name;
    private int tntAmount;
    private double explosionChance;

    public TNTCreeper() {
        super(EntityType.CREEPER, "tnt-creeper");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", "&4TNT Creeper"));
        tntAmount = cfg.getInt("tnt-amount", 2);
        explosionChance = cfg.getDouble("explosion-chance", 33.33) / 100;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        Item item = entity.getWorld().dropItem(entity.getLocation(), new ItemStack(Material.TNT));
        item.setCanMobPickup(false);
        item.setInvulnerable(true);
        entity.addPassenger(item);
        entity.addPotionEffect(INCREASE_DAMAGE);
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        if(!isThis(event.getEntity())) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        entity.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        Location loc = entity.getLocation();
        for(int i = 0; i < tntAmount; i++) {
            Entity tnt = entity.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
            tnt.setVelocity(new Vector(Rnd.nextDouble(2) - 1, 0.3, Rnd.nextDouble(2) - 1));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(explosionChance > 0 && isThis(event.getEntity()) && Rnd.chance(explosionChance))
            event.getDamager().getWorld().createExplosion(event.getDamager().getLocation(), 0.01f);
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
