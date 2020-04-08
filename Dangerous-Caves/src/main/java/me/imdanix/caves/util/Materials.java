package me.imdanix.caves.util;

import me.imdanix.caves.compatibility.VMaterial;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public final class Materials {
    public static final Material[] HELMETS;
    public static final Material[] LEGGINGS;
    public static final Material[] CHESTPLATES;
    public static final Material[] BOOTS;
    static {
        List<Material> helmets = new ArrayList<>();
        List<Material> leggings = new ArrayList<>();
        List<Material> chestplates = new ArrayList<>();
        List<Material> boots = new ArrayList<>();
        for (Material mat : Material.values()) {
            if (mat.isBlock()) continue;
            String name = mat.name();
            if (name.endsWith("_HELMET")) {
                helmets.add(mat);
            } else if (name.endsWith("_LEGGINGS")) {
                leggings.add(mat);
            } else if (name.endsWith("_CHESTPLATE")) {
                chestplates.add(mat);
            } else if (name.endsWith("_BOOTS")) {
                boots.add(mat);
            }
        }
        helmets.add(VMaterial.CARVED_PUMPKIN.get());
        HELMETS = helmets.toArray(new Material[0]);
        LEGGINGS = leggings.toArray(new Material[0]);
        CHESTPLATES = chestplates.toArray(new Material[0]);
        BOOTS = boots.toArray(new Material[0]);
    }

    public static ItemStack getColored(EquipmentSlot slot, int r, int g, int b) {
        ItemStack item;
        switch (slot) {
            default: return null;

            case HEAD:
                item = new ItemStack(Material.LEATHER_HELMET);
                break;

            case CHEST:
                item = new ItemStack(Material.LEATHER_CHESTPLATE);
                break;

            case LEGS:
                item = new ItemStack(Material.LEATHER_LEGGINGS);
                break;

            case FEET:
                item = new ItemStack(Material.LEATHER_BOOTS);
                break;
        }
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(r, g, b));
        item.setItemMeta(meta);
        return item;
    }
}
