package me.imdanix.caves.compatibility;

import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class PersistentTags implements TagsProvider {
    private final NamespacedKey MOB_KEY;

    public PersistentTags(Plugin plugin) {
        MOB_KEY = new NamespacedKey(plugin, "mob-type");
    }

    @Override
    public void setTag(LivingEntity entity, String tag) {
        entity.getPersistentDataContainer().set(MOB_KEY, PersistentDataType.STRING, tag);
    }

    @Override
    public void setTag(BlockState state, String tag) {
        if (!(state instanceof PersistentDataHolder)) return;
        ((PersistentDataHolder)state).getPersistentDataContainer().set(MOB_KEY, PersistentDataType.STRING, tag);
        state.update(false, false);
    }

    @Override
    public String getTag(LivingEntity entity) {
        return entity.getPersistentDataContainer().get(MOB_KEY, PersistentDataType.STRING);
    }

    @Override
    public boolean isTagged(LivingEntity entity) {
        return entity.getPersistentDataContainer().has(MOB_KEY, PersistentDataType.STRING);
    }

    @Override
    public boolean isTagged(LivingEntity entity, String tag) {
        String entityTag = getTag(entity);
        return tag.equals(entityTag);
    }

    @Override
    public String getTag(BlockState block) {
        if (!(block instanceof PersistentDataHolder)) return null;
        return ((PersistentDataHolder) block).getPersistentDataContainer().get(MOB_KEY, PersistentDataType.STRING);
    }
}
