package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.TickableMob;
import com.github.evillootlye.caves.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Watcher extends TickableMob implements Configurable, Listener {
    private static final ItemStack SKULL = new ItemStack(Material.PLAYER_HEAD, 1);
    static {
        SkullMeta meta = (SkullMeta) SKULL.getItemMeta();
        // TODO: PlayerProfile instead
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("8f83cb34-120d-40e5-9981-6c75729e3659")));
        SKULL.setItemMeta(meta);
    }
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, 30, 200);
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 80, 2);
    private static final Vector ZERO_VECTOR = new Vector(0, 0, 0);

    private int weight;

    public Watcher() {
        super(EntityType.HUSK, "watcher");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
    }

    @Override
    public void setup(LivingEntity entity) {
        entity.setSilent(true);
        entity.setCanPickupItems(false);
        entity.addPotionEffect(INVISIBILITY);

        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(SKULL);
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
            if(LocationUtils.isLookingAt(target, entity)) return;
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
