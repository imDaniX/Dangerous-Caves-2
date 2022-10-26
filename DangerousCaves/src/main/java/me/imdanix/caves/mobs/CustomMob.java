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
     * @return Is mob can be spawned there
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
    default void prepare(LivingEntity entity) {
        // Sometimes we just don't need to do so
    }

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
         * @param loc Where to spawn an entity
         * @return Spawned entity
         */
        LivingEntity spawn(Location loc);

        void tick(LivingEntity entity);
    }
}
