package me.imdanix.caves.util;

import me.imdanix.caves.compatibility.VMaterial;
import org.bukkit.Material;

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
        HELMETS = (Material[]) helmets.toArray();
        LEGGINGS = (Material[]) leggings.toArray();
        CHESTPLATES = (Material[]) chestplates.toArray();
        BOOTS = (Material[]) boots.toArray();
    }
}
