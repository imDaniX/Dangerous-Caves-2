package me.imdanix.caves.compatibility;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

public interface TagsProvider {
    void setTag(LivingEntity entity, String tag);

    void setTag(Block block, String tag);

    String getTag(LivingEntity entity);

    String getTag(Block block);
}
