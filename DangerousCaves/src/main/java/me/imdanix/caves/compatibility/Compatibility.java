package me.imdanix.caves.compatibility;

import io.papermc.lib.PaperLib;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

// TODO: Proper singleton for objects?
public final class Compatibility {
    private static PhysicalsProvider physicals;
    private static TagsProvider tags;

    public static void init(Plugin plugin) {
        int version = PaperLib.getMinecraftVersion();
        boolean isBukkit = !PaperLib.isSpigot();
        if (isBukkit)
            PaperLib.suggestPaper(plugin);
        if (version < 13) {
            if (version < 12)
                plugin.getLogger().warning("Please note that versions before 1.12.2 are not really supported.");
            physicals = new LegacyPhysicals();
            tags = new LegacyTags();
        } else if (version == 13) {
            physicals = new v1_13Physicals();
            tags = new LegacyTags();
        } else {
            physicals = version > 15 ? new v1_16Physicals() : new v1_13Physicals();
            tags = new PersistentTags(plugin);
        }
    }

    public static void cacheTag(String tag) {
        if (tags instanceof LegacyTags)
            ((LegacyTags) tags).cacheTag(tag);
    }

    public static ItemStack getHeadFromValue(String value) {
        return physicals.getHeadFromValue(value);
    }

    public static void rotate(Block block, BlockFace face) {
        physicals.rotate(block, face);
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

    public static boolean isTagged(LivingEntity entity) {
        return tags.isTagged(entity);
    }

    public static boolean isTagged(LivingEntity entity, String tag) {
        return tags.isTagged(entity, tag);
    }

    public static String getTag(Block block) {
        return tags.getTag(block);
    }

    public static int getMinY(World world) {
        return physicals.getMinY(world);
    }
}
