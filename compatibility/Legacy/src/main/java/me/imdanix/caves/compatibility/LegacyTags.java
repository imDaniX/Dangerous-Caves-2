package me.imdanix.caves.compatibility;

import org.bukkit.Nameable;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

public class LegacyTags implements TagsProvider {
    private static final String TAG_PREFIX = "§0§kdc-tag-";

    private final Set<String> tags;

    public LegacyTags() {
        tags = new HashSet<>();
    }

    public void cacheTag(String tag) {
        tags.add(tag);
    }

    @Override
    public void setTag(LivingEntity entity, String tag) {
        entity.addScoreboardTag(tag);
    }

    @Override
    public void setTag(BlockState state, String tag) {
        if (!(state instanceof Nameable)) return;
        ((Nameable)state).setCustomName(TAG_PREFIX + tag);
        state.update(false, false);
    }

    @Override
    public String getTag(LivingEntity entity) {
        Set<String> entityTags = entity.getScoreboardTags();

        if (entityTags.contains(DC_SCOREBOARD_TAG)) {
            if (entityTags.size() > tags.size()) {
                for (String tag : tags)
                    if (entityTags.contains(tag)) return tag;
            } else {
                for (String tag : entityTags)
                    if (tags.contains(tag)) return tag;
            }
        }
        return null;
    }

    @Override
    public boolean isTagged(LivingEntity entity) {
        return entity.getScoreboardTags().contains(DC_SCOREBOARD_TAG);
    }

    @Override
    public boolean isTagged(LivingEntity entity, String tag) {
        return entity.getScoreboardTags().contains(tag);
    }

    @Override
    public String getTag(BlockState state) {
        if (!(state instanceof Nameable)) return null;
        String name = ((Nameable) state).getCustomName();
        return name != null && name.startsWith(TAG_PREFIX) ? name.substring(TAG_PREFIX.length()) : null;
    }
}