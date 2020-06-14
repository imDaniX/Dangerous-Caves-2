/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
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

package me.imdanix.caves.mobs;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * Main interface for mobs
 */
public interface CustomMob {

    /**
     * Check if provided entity is this custom mob
     * @param entity Entity to check
     * @return Is entity a custom mob
     */
    boolean isThis(Entity entity);

    /**
     * Check if custom mob can be spawned on location
     * @param loc Where to check
     * @return Is mob can be spawned here
     */
    default boolean canSpawn(Location loc) {
        return true;
    }

    /**
     * Spawn a new {@link LivingEntity} of this custom mob
     * @param loc Where to spawn an entity
     * @return Spawned entity
     */
    LivingEntity spawn(Location loc);

    /**
     * Setup some mob settings (effects, equipment etc.)
     * @param entity Entity to setup
     */
    default void setup(LivingEntity entity) {}

    /**
     * Get base type of custom mob
     * @return Base type
     */
    EntityType getType();

    /**
     * Get custom type of custom mob
     * As for now there's no check, but should be lower-case-with-hyphens
     * @return Custom type
     */
    String getCustomType();

    /**
     * Get mob's weight in mobs pool
     * @return Mob's weight
     */
    int getWeight();

    interface Ticking extends CustomMob {
        /**
         * Spawn a new {@link LivingEntity} of this custom mob
         * Make sure to handle it via {@link MobsManager#handle(LivingEntity, Ticking)}
         * @param loc Where to spawn an entity
         * @return Spawned entity
         */
        LivingEntity spawn(Location loc);

        void tick(LivingEntity entity);
    }
}
