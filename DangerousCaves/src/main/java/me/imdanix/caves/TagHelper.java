package me.imdanix.caves;

import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class TagHelper {
    public static final String SCOREBOARD_TAG = "dc-mob";
    private final NamespacedKey MOB_KEY;

    public TagHelper(Plugin plugin) {
        MOB_KEY = new NamespacedKey(plugin, "mob-type");
    }

    public void setTag(LivingEntity entity, String tag) {
        entity.getPersistentDataContainer().set(MOB_KEY, PersistentDataType.STRING, tag);
    }

    public void setTag(BlockState state, String tag) {
        if (!(state instanceof PersistentDataHolder)) return;
        ((PersistentDataHolder)state).getPersistentDataContainer().set(MOB_KEY, PersistentDataType.STRING, tag);
        state.update(false, false);
    }

    public String getTag(LivingEntity entity) {
        return entity.getPersistentDataContainer().get(MOB_KEY, PersistentDataType.STRING);
    }

    public boolean isTagged(LivingEntity entity) {
        return entity.getPersistentDataContainer().has(MOB_KEY, PersistentDataType.STRING);
    }

    public boolean isTagged(LivingEntity entity, String tag) {
        String entityTag = getTag(entity);
        return tag.equals(entityTag);
    }

    public String getTag(BlockState block) {
        if (!(block instanceof PersistentDataHolder)) return null;
        return ((PersistentDataHolder) block).getPersistentDataContainer().get(MOB_KEY, PersistentDataType.STRING);
    }

    public static String mobScoreboardTag(String id) {
        return SCOREBOARD_TAG + "-" + id;
    }
}
