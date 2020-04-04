package me.imdanix.caves.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class Compatibility {
    private static MaterialsProvider materials;
    private static TagsProvider tags;

    public static void init() {
        int version = Integer.parseInt(Bukkit.getVersion().split("\\.")[1], 12);
        if(version < 13) {
            materials = new LegacyMaterials();
            tags = new EffectTags();
        } else if(version == 13) {
            materials = new FlattenedMaterials();
            tags = new EffectTags();
        } else {
            materials = new FlattenedMaterials();
            tags = new PersistantTags();
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
