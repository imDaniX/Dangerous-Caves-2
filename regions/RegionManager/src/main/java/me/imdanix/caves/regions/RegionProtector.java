package me.imdanix.caves.regions;

import org.bukkit.Location;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface RegionProtector extends BiPredicate<CheckType, Location> {
    /**
     * Called on plugin enable
     */
    default void onEnable() {
        // Not needed by default
    }

    /**
     * Get name of this region manager to use it in testing
     * @return Name of this region manager
     */
    default String getName() {
        return "none";
    }

    /**
     * Check possibility of certain action in the location
     * @param type Type of action to test
     * @param location Location to test
     * @return Is action allowed here
     */
    @Override
    boolean test(CheckType type, Location location);
}
