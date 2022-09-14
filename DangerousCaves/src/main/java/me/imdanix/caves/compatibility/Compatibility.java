package me.imdanix.caves.compatibility;

import io.papermc.lib.PaperLib;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
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

    public static void setTag(LivingEntity entity, String tag) {
        entity.addScoreboardTag(TagsProvider.DC_SCOREBOARD_TAG);
        tags.setTag(entity, tag);
    }

    public static void setTag(BlockState block, String tag) {
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

    public static String getTag(BlockState block) {
        return tags.getTag(block);
    }

    public static int getMinY(World world) {
        return physicals.getMinY(world);
    }
}
