package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.compatibility.VSound;
import me.imdanix.caves.mobs.MobBase;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.PlayerAttackedEvent;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HungeringDarkness extends MobBase implements Listener {
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3, false, false);

    private double damage;
    private boolean remove;
    private boolean vision;
    private boolean deathSound;

    public HungeringDarkness() {
        super(EntityType.VEX, "hungering-darkness", 8);
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        damage = cfg.getDouble("damage", 200);
        remove = cfg.getBoolean("remove-on-light", false);
        vision = cfg.getBoolean("night-vision", false);
        deathSound = cfg.getBoolean("death-sound", true);
    }

    @Override
    public boolean canSpawn(Location location) {
        return location.getBlock().getLightLevel() == 0;
    }

    @Override
    public void setup(LivingEntity entity) {
        entity.setCustomNameVisible(false);
        entity.setInvulnerable(true);
        entity.setCollidable(false);

        entity.addPotionEffect(INVISIBILITY);
        entity.addPotionEffect(SLOW);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        if (!isThis(event.getEntity()) || target == null) return;
        if (target.getLocation().getBlock().getLightLevel() > 0 && (
                !vision ||
                !(target instanceof LivingEntity) ||
                ((LivingEntity)target).hasPotionEffect(PotionEffectType.NIGHT_VISION)
            )) {
            event.setCancelled(true);
            die(event.getEntity());
        }
    }

    @EventHandler
    public void onAttack(PlayerAttackedEvent event) {
        if (!isThis(event.getAttacker())) return;
        Player player = event.getPlayer();
        if (player.getLocation().getBlock().getLightLevel() > 0 && (
                !vision ||
                player.hasPotionEffect(PotionEffectType.NIGHT_VISION)
            )) {
            event.setCancelled(true);
            die(event.getAttacker());
        } else event.setDamage(damage);
    }

    private void die(Entity entity) {
        if (!remove) return;
        if (deathSound) {
            Locations.playSound(entity.getLocation(), VSound.ENTITY_PHANTOM_SWOOP.get(), 1f, 0.8f);
        }
        entity.remove();
    }
}
