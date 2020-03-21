package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.TickableMob;
import com.github.evillootlye.caves.util.Locations;
import com.github.evillootlye.caves.util.Materials;
import com.github.evillootlye.caves.util.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Watcher extends TickableMob implements Configurable, Listener {
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, 30, 200);
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 80, 2);
    private static final Vector ZERO_VECTOR = new Vector(0, 0, 0);

    private int weight;
    private String name;
    private ItemStack head;

    public Watcher() {
        super(EntityType.HUSK, "watcher");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", ""));
        head = Materials.getHeadFromValue(cfg.getString("head-value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDI5MzhmMjQxZDc0NDMzZjcyZjVjMzljYjgzYThlNWZmN2UxNzdiYTdjYjQyODY5ZGI2NGUzMDc5MTAyYmZjNSJ9fX0="));
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        entity.setSilent(true);
        entity.setCanPickupItems(false);
        entity.addPotionEffect(INVISIBILITY);

        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(head);
        equipment.setHelmetDropChance(0);
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if(isThis(event.getEntity()))
            event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_SLIME_SQUISH, 1, 1.1f);
    }

    @Override
    public void tick(LivingEntity entity) {
        LivingEntity target = ((Monster)entity).getTarget();
        if(target instanceof Player) {
            if(Locations.isLookingAt(target, entity)) return;
            Location loc = target.getLocation().add(target.getLocation().getDirection());
            loc.setYaw(-loc.getYaw());
            entity.teleport(loc);
            target.setVelocity(ZERO_VECTOR);
            target.addPotionEffect(SLOW);
            target.addPotionEffect(BLINDNESS);
            target.getWorld().playSound(target.getEyeLocation(), Sound.ENTITY_GHAST_HURT, 1, 2);
        }
    }
}
