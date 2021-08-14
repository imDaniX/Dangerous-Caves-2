package me.imdanix.caves.compatibility;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

public interface TagsProvider {
    String DC_SCOREBOARD_TAG = "dc-mob";

    void setTag(LivingEntity entity, String tag);

    void setTag(Block block, String tag);

    String getTag(LivingEntity entity);

    boolean isTagged(LivingEntity entity);

    boolean isTagged(LivingEntity entity, String tag);

    String getTag(Block block);
}
