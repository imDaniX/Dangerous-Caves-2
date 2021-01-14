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

package me.imdanix.caves.util.bound;

public interface Bound {
    boolean isInside(int x, int z);

    static Bound fromString(String boundStr) {
        String[] xzStr = boundStr.split(" ");
        String[] firstCoords = xzStr[0].split(",");
        if (xzStr.length > 1) {
            String[] secondCoords = xzStr[1].split(",");
            try {
                return new DualBound(
                        Integer.parseInt(firstCoords[0]), Integer.parseInt(secondCoords[0]),
                        Integer.parseInt(firstCoords[1]), Integer.parseInt(secondCoords[1])
                );
            } catch (NumberFormatException ignored) {}
        } else {
            try {
                return new SingularBound(
                        Integer.parseInt(firstCoords[0]), Integer.parseInt(firstCoords[1])
                );
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
