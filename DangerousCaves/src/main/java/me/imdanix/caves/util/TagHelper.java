package me.imdanix.caves.util;

import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public final class TagHelper {
    public static final NamespacedKey MOB_KEY = Objects.requireNonNull(NamespacedKey.fromString("dangerouscaves:mob-type"));
    public static final String SCOREBOARD_TAG = "dc-mob";

    private TagHelper() {}

    public static void setTag(LivingEntity entity, String tag) {
        entity.getPersistentDataContainer().set(MOB_KEY, PersistentDataType.STRING, tag);
        entity.addScoreboardTag(SCOREBOARD_TAG);
        entity.addScoreboardTag(mobScoreboardTag(tag));
    }

    public static void setTag(BlockState state, String tag) {
        if (state instanceof PersistentDataHolder dataHolder) {
            dataHolder.getPersistentDataContainer().set(MOB_KEY, PersistentDataType.STRING, tag);
            state.update(false, false);
        }
    }

    public static String getTag(LivingEntity entity) {
        return entity.getPersistentDataContainer().get(MOB_KEY, PersistentDataType.STRING);
    }

    public static boolean isTagged(LivingEntity entity) {
        return entity.getPersistentDataContainer().has(MOB_KEY, PersistentDataType.STRING);
    }

    public static boolean isTagged(LivingEntity entity, String tag) {
        String entityTag = getTag(entity);
        return tag.equals(entityTag);
    }

    public static String getTag(BlockState block) {
        if (block instanceof PersistentDataHolder dataHolder) {
            return dataHolder.getPersistentDataContainer().get(MOB_KEY, PersistentDataType.STRING);
        }
        return null;
    }

    public static String mobScoreboardTag(String id) {
        return SCOREBOARD_TAG + "-" + id;
    }
}
