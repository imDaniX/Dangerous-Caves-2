package com.github.evillootlye.caves.utils;

import org.bukkit.Material;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class MaterialUtils {
    public static final Material[] HELMETS;
    public static final Material[] LEGGINGS;
    public static final Material[] CHESTPLATES;
    public static final Material[] BOOTS;
    static {
        Set<Material> helmets = new HashSet<>();
        Set<Material> leggings = new HashSet<>();
        Set<Material> chestplates = new HashSet<>();
        Set<Material> boots = new HashSet<>();
        for(Material mat : Material.values()) {
            if(mat.isBlock()) continue;
            String name = mat.name();
            if(name.endsWith("_HELMET")) {
                helmets.add(mat);
            } else if(name.endsWith("_LEGGINGS")) {
                leggings.add(mat);
            } else if(name.endsWith("_CHESTPLATE")) {
                chestplates.add(mat);
            } else if(name.endsWith("_BOOTS")) {
                boots.add(mat);
            }
        }
        helmets.add(Material.CARVED_PUMPKIN);
        HELMETS = (Material[]) helmets.toArray();
        LEGGINGS = (Material[]) leggings.toArray();
        CHESTPLATES = (Material[]) chestplates.toArray();
        BOOTS = (Material[]) boots.toArray();
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
