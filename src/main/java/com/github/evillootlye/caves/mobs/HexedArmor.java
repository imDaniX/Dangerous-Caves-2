package com.github.evillootlye.caves.mobs;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.utils.MaterialUtils;
import com.github.evillootlye.caves.utils.PlayerAttackedEvent;
import com.github.evillootlye.caves.utils.Rnd;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Configurable.Path("mobs.hexed-armor")
public class HexedArmor extends CustomMob implements Listener {
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false);

    public HexedArmor() {
        super(EntityType.ZOMBIE, "hexed-armor");
    }

    @Override
    public void setup(LivingEntity entity) {
        entity.addPotionEffect(INVISIBILITY);
        entity.setSilent(true);
        entity.setCanPickupItems(false);

        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(getRandom(MaterialUtils.HELMETS));
        equipment.setChestplate(getRandom(MaterialUtils.CHESTPLATES));
        equipment.setLeggings(getRandom(MaterialUtils.LEGGINGS));
        equipment.setBoots(getRandom(MaterialUtils.BOOTS));
    }

    @EventHandler
    public void onAttack(PlayerAttackedEvent event) {
        LivingEntity entity = event.getAttacker();
        if (isThis(entity) && Rnd.nextDouble() < 0.2) {
            PlayerInventory inv = event.getPlayer().getInventory();
            ItemStack[] armor = inv.getArmorContents();
            for (ItemStack i2 : armor) {
                if (i2 != null && i2.getType() != Material.AIR) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), i2);
                }
            }
            inv.setArmorContents(entity.getEquipment().getArmorContents());
            entity.getEquipment().clear();
            entity.remove();
        }
    }

    private static ItemStack getRandom(Material[] arr) {
        int i = Rnd.nextInt(arr.length + 2);
        return i >= arr.length ? new ItemStack(Material.AIR) : enchant(new ItemStack(arr[i]));
    }

    private static ItemStack enchant(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }
}
