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

package me.imdanix.caves.util.random;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

/******************************************************************************
 * File: AliasMethod.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of the alias method implemented using Vose's algorithm.
 * The alias method allows for efficient sampling of random values from a
 * discrete probability distribution (i.e. rolling a loaded die) in O(1) time
 * each after O(n) preprocessing time.
 *
 * For a complete writeup on the alias method, including the intuition and
 * important proofs, please see the article "Darts, Dice, and Coins: Smpling
 * from a Discrete Distribution" at
 *
 *                 http://www.keithschwarz.com/darts-dice-coins/
 */

public final class AliasMethod<T> {

    private final List<T> elements;

    private final int[] alias;
    private final double[] probability;

    public AliasMethod(Collection<T> collection, ToDoubleFunction<T> funct) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(funct);

        if (collection.isEmpty())
            throw new IllegalArgumentException("Probability vector must be nonempty.");

        double[] probabilities = new double[collection.size()];
        elements = new ArrayList<>();

        double sum = 0;
        for(T item : collection) {
            elements.add(item);
            sum += funct.applyAsDouble(item);
        }
        for(int i = 0; i < elements.size(); i++) {
            probabilities[i] = funct.applyAsDouble(elements.get(i)) / sum;
        }

        probability = new double[probabilities.length];
        alias = new int[probabilities.length];

        final double average = 1.0 / probabilities.length;

        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> large = new ArrayDeque<>();

        for (int i = 0; i < probabilities.length; ++i) {
            if (probabilities[i] >= average)
                large.add(i);
            else
                small.add(i);
        }

        while (!small.isEmpty() && !large.isEmpty()) {
            int less = small.removeLast();
            int more = large.removeLast();

            probability[less] = probabilities[less] * probabilities.length;
            alias[less] = more;

            probabilities[more] = probabilities[more] + probabilities[less] - average;

            if (probabilities[more] >= 1.0 / probabilities.length)
                large.add(more);
            else
                small.add(more);
        }

        while (!small.isEmpty())
            probability[small.removeLast()] = 1.0;
        while (!large.isEmpty())
            probability[large.removeLast()] = 1.0;
    }

    public T next() {
        int column = Rnd.nextInt(probability.length);
        boolean coinToss = Rnd.chance(probability[column]);
        return elements.get(coinToss ? column : alias[column]);
    }
}
