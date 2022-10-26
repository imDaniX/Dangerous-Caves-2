package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.mobs.MobBase;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.PlayerAttackedEvent;
import me.imdanix.caves.util.random.Rng;
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

public class HexedArmor extends MobBase implements Listener {
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false);

    private double chance;
    private boolean binding;

    public HexedArmor() {
        super(EntityType.ZOMBIE, "hexed-armor", 6);
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        binding = cfg.getBoolean("binding-curse", true);
        chance = cfg.getDouble("apply-chance", 25) / 100;
    }

    @Override
    public void prepare(LivingEntity entity) {
        entity.addPotionEffect(INVISIBILITY);
        entity.setSilent(true);
        entity.setCanPickupItems(false);

        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(getRandom(Materials.HELMETS));
        equipment.setChestplate(getRandom(Materials.CHESTPLATES));
        equipment.setLeggings(getRandom(Materials.LEGGINGS));
        equipment.setBoots(getRandom(Materials.BOOTS));
    }

    @EventHandler
    public void onAttack(PlayerAttackedEvent event) {
        LivingEntity entity = event.getAttacker();
        if (isThis(entity) && Rng.chance(chance)) {
            PlayerInventory inv = event.getPlayer().getInventory();
            ItemStack[] armor = inv.getArmorContents();
            for (ItemStack item : armor) {
                if (item != null && item.getType() != Material.AIR) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), item);
                }
            }
            inv.setArmorContents(entity.getEquipment().getArmorContents());
            entity.getEquipment().clear();
            entity.remove();
        }
    }

    private ItemStack getRandom(List<Material> armor) {
        int i = Rng.nextInt(armor.size());
        return i >= armor.size() ? null : enchant(new ItemStack(armor.get(i)));
    }

    private ItemStack enchant(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (binding) meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }
}
