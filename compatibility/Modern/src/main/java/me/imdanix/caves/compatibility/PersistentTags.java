/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2021  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    public String getTag(Block block) {
        if (!(block.getState() instanceof PersistentDataHolder)) return null;
        return ((PersistentDataHolder)block.getState()).getPersistentDataContainer().get(MOB_KEY, PersistentDataType.STRING);
    }
}
