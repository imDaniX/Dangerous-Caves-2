/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2021  imDaniX
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

package me.imdanix.caves.compatibility;

import org.bukkit.Material;

public enum VMaterial {
    MAGMA_BLOCK("MAGMA_BLOCK", "MAGMA"),
    REDSTONE_TORCH("REDSTONE_TORCH", "REDSTONE_TORCH_ON"),
    ANDESITE("ANDESITE", "COBBLESTONE"),
    COBBLESTONE_WALL("COBBLESTONE_WALL", "COBBLE_WALL"),
    STONE_PRESSURE_PLATE("STONE_PRESSURE_PLATE", "STONE_PLATE"),
    COBWEB("COBWEB", "WEB"),
    SPRUCE_PLANKS("SPRUCE_PLANKS", "WOOD"),
    SKELETON_SKULL_BLOCK("SKELETON_SKULL", "SKULL"),
    CRAFTING_TABLE("CRAFTING_TABLE", "WORKBENCH"),
    COBBLESTONE_SLAB("COBBLESTONE_SLAB", "STONE_SLAB2", "STONE_SLAB"),
    OAK_PLANKS("OAK_PLANKS", "WOOD"),
    SPRUCE_LOG("SPRUCE_LOG", "LOG"),
    SPAWNER("SPAWNER", "MOB_SPAWNER"),
    INFESTED_STONE("INFESTED_STONE", "MONSTER_EGGS"),
    STONE_BRICKS("STONE_BRICKS", "SMOOTH_BRICK"),
    CRACKED_STONE_BRICKS("CRACKED_STONE_BRICKS", "SMOOTH_BRICK"),
    POLISHED_ANDESITE("POLISHED_ANDESITE", "SMOOTH_BRICK"),
    STONE_BRICK_SLAB("STONE_BRICK_SLAB", "STONE_SLAB2", "STONE_SLAB"),
    CARVED_PUMPKIN("CARVED_PUMPKIN", "PUMPKIN");

    private final Material type;

    VMaterial(String... typesStr) {
        for (String typeStr : typesStr) {
            Material type = Material.getMaterial(typeStr);
            if (type != null) {
                this.type = type;
                return;
            }
        }
        this.type = null;
    }

    public Material get() {
        return type;
    }
}
