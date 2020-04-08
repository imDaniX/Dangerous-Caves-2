package me.imdanix.caves.compatibility;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
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
    public void setTag(Block block, String tag) {
        BlockState state = block.getState();
        if(!(state instanceof PersistentDataHolder)) return;
        ((PersistentDataHolder)state).getPersistentDataContainer().set(MOB_KEY, PersistentDataType.STRING, tag);
        state.update();
    }

    @Override
    public String getTag(LivingEntity entity) {
        return entity.getPersistentDataContainer().get(MOB_KEY, PersistentDataType.STRING);
    }

    @Override
    public String getTag(Block block) {
        if(!(block.getState() instanceof PersistentDataHolder)) return null;
        return ((PersistentDataHolder)block.getState()).getPersistentDataContainer().get(MOB_KEY, PersistentDataType.STRING);
    }
}
