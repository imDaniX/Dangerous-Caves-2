package com.github.evillootlye.caves.util;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class Materials {
    public static final List<Material> HELMETS;
    public static final List<Material> LEGGINGS;
    public static final List<Material> CHESTPLATES;
    public static final List<Material> BOOTS;
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
        helmets.add(Material.CARVED_PUMPKIN);
        HELMETS = Collections.unmodifiableList(helmets);
        LEGGINGS = Collections.unmodifiableList(leggings);
        CHESTPLATES = Collections.unmodifiableList(chestplates);
        BOOTS = Collections.unmodifiableList(boots);
    }
    public static final Set<Material> CAVE = Collections.unmodifiableSet(EnumSet.of(
            Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE,
            Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.COAL_ORE,
            Material.COBBLESTONE, Material.MOSSY_COBBLESTONE,
            Material.DIRT, Material.GRAVEL, Material.OBSIDIAN, Material.OAK_PLANKS, Material.BEDROCK,
            Material.SOUL_SAND, Material.NETHERRACK, Material.NETHER_BRICKS
    ));
}
