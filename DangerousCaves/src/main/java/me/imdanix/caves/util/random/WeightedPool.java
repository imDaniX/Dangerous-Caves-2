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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * Weighted collection class based off DarkSeraphim's answer
 * https://www.spigotmc.org/threads/probability.449617/#post-3868549
 */
public class WeightedPool<T> {
    private final List<T> elements;

    public WeightedPool() {
        elements = new ArrayList<>();
    }

    public WeightedPool(Collection<T> collection, ToIntFunction<T> funct) {
        elements = new ArrayList<>();
        if (!collection.isEmpty()) {
            Objects.requireNonNull(funct);
            collection.forEach(t -> add(t, funct.applyAsInt(t)));
        }

    }

    public void add(T element, int weight) {
        for (int i = 0; i < weight; i++)
            this.elements.add(element);
    }

    public T next() {
        return this.elements.get(Rng.nextInt(this.elements.size()));
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }
}
