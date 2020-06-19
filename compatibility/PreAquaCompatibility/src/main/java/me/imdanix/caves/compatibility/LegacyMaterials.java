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

package me.imdanix.caves.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Vine;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class LegacyMaterials implements MaterialsProvider {

    private static final Set<Material> CAVE = Collections.unmodifiableSet(EnumSet.of(
            Material.STONE,
            Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.COAL_ORE,
            Material.COBBLESTONE, Material.MOSSY_COBBLESTONE,
            Material.DIRT, Material.GRAVEL, Material.OBSIDIAN, Material.WOOD, Material.BEDROCK,
            Material.SOUL_SAND, Material.NETHERRACK, Material.NETHER_BRICK,
            Material.ENDER_STONE
    ));

    public boolean isAir(Material type) {
        return type == Material.AIR;
    }

    public boolean isCave(Material type) {
        return CAVE.contains(type);
    }

    @Override
    public void rotate(Block block, BlockFace face) {
        BlockState state = block.getState();
        MaterialData data = state.getData();
        if(data instanceof Directional) {
            ((Directional) data).setFacingDirection(face);
        } else if(data instanceof Vine) {
            ((Vine) data).putOnFace(face);
        }
        state.update(false, false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getHeadFromValue(String value) {
        return Bukkit.getUnsafe().modifyItemStack(
                new ItemStack(Material.SKULL_ITEM, 1, (short) 3),
                "{SkullOwner:{Id:\"" + UUID.nameUUIDFromBytes(value.getBytes()) + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }
}

