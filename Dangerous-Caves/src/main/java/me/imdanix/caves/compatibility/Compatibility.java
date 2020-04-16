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
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Compatibility {
    private static MaterialsProvider materials;
    private static TagsProvider tags;

    public static void init(Plugin plugin) {
        int version = Integer.parseInt(Bukkit.getVersion().split("\\.")[1]);
        if(version < 13) {
            plugin.getLogger().info("Using LegacyMaterials(up to 1.12.2) with EffectTags(up to 1.13.2)");
            materials = new LegacyMaterials();
            tags = new EffectTags();
        } else if(version == 13) {
            plugin.getLogger().info("Using FlattenedMaterials(from 1.13) with EffectTags(up to 1.13.2)");
            materials = new FlattenedMaterials();
            tags = new EffectTags();
        } else {
            plugin.getLogger().info("Using FlattenedMaterials(from 1.13) with PersistentTags(from 1.14)");
            materials = new FlattenedMaterials();
            tags = new PersistentTags(plugin);
        }
    }

    public static void cacheTag(String tag) {
        if(tags instanceof EffectTags)
            ((EffectTags) tags).cacheTag(tag);
    }

    public static boolean isAir(Material type) {
        return materials.isAir(type);
    }

    public static boolean isCave(Material type) {
        return materials.isCave(type);
    }

    public static ItemStack getHeadFromValue(String value) {
        return materials.getHeadFromValue(value);
    }

    public static void rotate(Block block, BlockFace face) {
        materials.rotate(block, face);
    }

    public static void setTag(LivingEntity entity, String tag) {
        tags.setTag(entity, tag);
    }

    public static void setTag(Block block, String tag) {
        tags.setTag(block, tag);
    }

    public static String getTag(LivingEntity entity) {
        return tags.getTag(entity);
    }

    public static String getTag(Block block) {
        return tags.getTag(block);
    }

}
