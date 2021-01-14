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

package me.imdanix.caves.util.random;

public class PseudoRandom {
    private static final int[] ZERO_INT = null;

    public static PseudoRandom ZERO_PSEUDO_RANDOM = new PseudoRandom(){
        @Override
        public int next() {
            return 0;
        }
    };

    private final int[] randomValues;
    private int cur = 0;

    private PseudoRandom() {
        this.randomValues = ZERO_INT;
    }

    public PseudoRandom(int[] randomValues) {
        this.randomValues = randomValues;
    }

    public int next() {
        return randomValues[cur >= randomValues.length ? (cur = 0) : cur++];
    }
}
