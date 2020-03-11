package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.CustomMob;
import com.github.evillootlye.caves.util.Materials;
import com.github.evillootlye.caves.util.PlayerAttackedEvent;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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

import java.util.List;

@Configurable.Path("mobs.hexed-armor")
public class HexedArmor extends CustomMob implements Listener, Configurable {
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false);

    private int weight;

    private double chance;

    public HexedArmor() {
        super(EntityType.ZOMBIE, "hexed-armor");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        chance = cfg.getDouble("apply-chance", 25) / 100;
    }

    @Override
    public void setup(LivingEntity entity) {
        entity.addPotionEffect(INVISIBILITY);
        entity.setSilent(true);
        entity.setCanPickupItems(false);

        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(getRandom(Materials.HELMETS));
        equipment.setChestplate(getRandom(Materials.CHESTPLATES));
        equipment.setLeggings(getRandom(Materials.LEGGINGS));
        equipment.setBoots(getRandom(Materials.BOOTS));
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @EventHandler
    public void onAttack(PlayerAttackedEvent event) {
        LivingEntity entity = event.getAttacker();
        if (isThis(entity) && Rnd.nextDouble() < chance) {
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

    private static ItemStack getRandom(List<Material> arr) {
        int i = Rnd.nextInt(arr.size() + 1);
        return i >= arr.size() ? new ItemStack(Material.AIR) : enchant(new ItemStack(arr.get(i)));
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
