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

public class SingularBound implements Bound {
    private final int x;
    private final int z;

    public SingularBound(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean isInside(int x, int z) {
        return this.x == x && this.z == z;
    }

    @Override
    public int hashCode() {
        return (x >>> 15) * (z >>> 31) * 3343;
    }

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof SingularBound)) return false;
        SingularBound b = (SingularBound) object;
        return x == b.x && z == b.z;
    }
}
