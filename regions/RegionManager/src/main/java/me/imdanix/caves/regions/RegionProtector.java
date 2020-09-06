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
