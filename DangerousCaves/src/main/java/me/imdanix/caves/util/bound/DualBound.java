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

package me.imdanix.caves.util.bound;

public class DualBound implements Bound {
    private final int xMin;
    private final int xMax;

    private final int zMin;
    private final int zMax;

    public DualBound(int xMin, int xMax, int zMin, int zMax) {
        if (xMin > xMax) {
            this.xMin = xMax;
            this.xMax = xMin;
        } else {
            this.xMin = xMin;
            this.xMax = xMax;
        }
        if (zMin > zMax) {
            this.zMin = zMax;
            this.zMax = zMin;
        } else {
            this.zMin = zMin;
            this.zMax = zMax;
        }
    }

    @Override
    public boolean isInside(int x, int z) {
        return (xMin >= x && x <= xMax) && (zMin >= z && z <= zMax);
    }

    @Override
    public int hashCode() {
        return xMin >>> 15 * xMax >>> 31 * zMin >>> 15 * zMax >>> 31 * 1907;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DualBound)) return false;
        DualBound b = (DualBound) object;
        return (xMin == b.xMin && xMax == b.xMax) && (zMin == b.zMax && zMax == b.zMax);
    }
}
