package com.github.evillootlye.caves.mobs;

import com.github.evillootlye.caves.PlayerAttackedEvent;
import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.utils.Rnd;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Configurable.Path("mobs.magma-monster")
public class MagmaMonster extends CustomMob implements Listener {
    private static final PotionEffect FIRE_RESISTANCE = new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
            Integer.MAX_VALUE, 1, false, false);
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY,
            Integer.MAX_VALUE, 1, false, false);

    private static final ItemStack CHESTPLATE;
    private static final ItemStack BOOTS;
    private static final ItemStack LEGGINGS;
    private static final ItemStack POWDER;
    static {
        CHESTPLATE = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) CHESTPLATE.getItemMeta();
        meta.setColor(Color.fromRGB(252, 115, 69));
        CHESTPLATE.setItemMeta(meta);
        BOOTS = new ItemStack(Material.LEATHER_LEGGINGS);
        BOOTS.setItemMeta(meta);
        LEGGINGS = new ItemStack(Material.LEATHER_BOOTS);
        LEGGINGS.setItemMeta(meta);
        POWDER = new ItemStack(Material.BLAZE_POWDER);
    }

    public MagmaMonster() {
        super(EntityType.ZOMBIE, "magma-monster");
    }

    @Override
    public void setup(LivingEntity entity) {
        entity.addPotionEffect(FIRE_RESISTANCE);
        entity.addPotionEffect(INVISIBILITY);
        entity.setFireTicks(Integer.MAX_VALUE);
        entity.setSilent(true);
        entity.setCanPickupItems(false);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInMainHand(POWDER);
        equipment.setItemInOffHand(POWDER);
        equipment.setChestplate(CHESTPLATE);
        equipment.setLeggings(LEGGINGS);
        equipment.setBoots(BOOTS);
    }

    @EventHandler
    public void onAttack(PlayerAttackedEvent event) {
        if(!isThis(event.getAttacker()) || Rnd.nextBoolean()) return;
        event.getPlayer().setFireTicks(60);
    }
}
