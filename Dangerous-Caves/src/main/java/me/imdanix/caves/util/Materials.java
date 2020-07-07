/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.caves.util;

import me.imdanix.caves.compatibility.VMaterial;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

    private static final Set<Material> CAVE = new Materials.SetBuilder(
            Material.STONE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.COAL_ORE,
            Material.COBBLESTONE, Material.MOSSY_COBBLESTONE,
            Material.DIRT, Material.GRAVEL, Material.OBSIDIAN, Material.BEDROCK,
            Material.SOUL_SAND, Material.NETHERRACK
    ).with(
            "ANDESITE", "DIORITE", "GRANITE", "SOUL_SOIL", "NETHER_GOLD_ORE"
    ).with(
            or("OAK_PLANKS", "WOOD"), or("END_STONE", "ENDER_STONE")
    ).build();

    private static final Set<Material> AIR = new Materials.SetBuilder(
            "AIR", "CAVE_AIR", "VOID_AIR"
    ).build();

    public static boolean isAir(Material type) {
        return AIR.contains(type);
    }

    public static boolean isCave(Material type) {
        return CAVE.contains(type);
    }

    public static Set<Material> getSet(Collection<String> typeColl) {
        Set<Material> materials = new HashSet<>();
        for(String typeStr : typeColl) {
            Material type = Material.getMaterial(typeStr.toUpperCase(Locale.ENGLISH));
            if(type != null) materials.add(type);
        }
        return materials.isEmpty() ? Collections.emptySet() : materials;
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

    public static Material or(String... typesStr) {
        for(String typeStr : typesStr) {
            Material type = Material.getMaterial(typeStr.toUpperCase(Locale.ENGLISH));
            if(type != null) return type;
        }
        return null;
    }

    public static class SetBuilder {
        private final Set<Material> materials;

        public SetBuilder() {
            materials = new HashSet<>();
        }

        public SetBuilder(Material... types) {
            materials = new HashSet<>();
            with(types);
        }

        public SetBuilder(String... types) {
            materials = new HashSet<>();
            with(types);
        }

        public SetBuilder with(String... types) {
            materials.addAll(Materials.getSet(Arrays.asList(types)));
            return this;
        }

        public SetBuilder with(Material... types) {
            for(Material type : types) {
                if(type != null)
                    materials.add(type);
            }
            return this;
        }

        public EnumSet<Material> build() {
            return EnumSet.copyOf(materials);
        }
    }
}
