package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.CustomMob;
import com.github.evillootlye.caves.utils.PlayerAttackedEvent;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HungeringDarkness extends CustomMob.TickableMob implements Listener, Configurable {
    private final static PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);
    private final static PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3, false, false);
    private int weight;
    private double damage;
    private boolean remove;

    public HungeringDarkness() {
        super(EntityType.HUSK, "hungering-darkness");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        damage = cfg.getDouble("damage", 200);
        remove = cfg.getBoolean("remove-on-light", true);
    }

    @Override
    public boolean canSpawn(EntityType type, Location location) {
        return location.getBlock().getLightFromBlocks() == 0;
    }

    @Override
    public void setup(LivingEntity entity) {
        entity.setSilent(true);
        entity.setInvulnerable(true);
        entity.setCanPickupItems(false);
        entity.setCustomNameVisible(false);
        entity.setCollidable(false);

        entity.addPotionEffect(INVISIBILITY);
        entity.addPotionEffect(SLOW);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTarget(EntityTargetEvent event) {
        if(!isThis(event.getEntity()) || event.getReason() != EntityTargetEvent.TargetReason.CLOSEST_PLAYER) return;
        if(event.getTarget().getLocation().getBlock().getLightLevel() > 0) {
            if(remove)
                event.getEntity().remove();
            else
                event.setTarget(null);
        }
    }

    @EventHandler
    public void onAttack(PlayerAttackedEvent event) {
        if(!isThis(event.getAttacker())) return;
        if(event.getPlayer().getLocation().getBlock().getLightLevel() > 0) {
            event.setCancelled(true);
            event.getAttacker().remove();
        } else event.setDamage(damage);
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void tick(Entity entity) {
        if(entity.getLocation().getBlock().getLightLevel() > 0) entity.remove();
    }
}
