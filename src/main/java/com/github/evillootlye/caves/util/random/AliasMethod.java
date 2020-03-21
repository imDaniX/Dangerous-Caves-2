package com.github.evillootlye.caves.util.random;

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

    private final int[] alias;
    private final double[] probability;

    private final List<T> items;

    public AliasMethod(Collection<T> collection, ToDoubleFunction<T> funct) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(funct);

        if (collection.isEmpty())
            throw new IllegalArgumentException("Probability vector must be nonempty.");

        List<Double> probabilities = new ArrayList<>();
        items = new ArrayList<>();
        double sum = 0;
        for(T item : collection) {
            items.add(item);
            sum += funct.applyAsDouble(item);
        }
        for(T item : items) {
            probabilities.add(funct.applyAsDouble(item) / sum);
        }

        probability = new double[probabilities.size()];
        alias = new int[probabilities.size()];

        final double average = 1.0 / probabilities.size();

        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> large = new ArrayDeque<>();

        for (int i = 0; i < probabilities.size(); ++i) {
            if (probabilities.get(i) >= average)
                large.add(i);
            else
                small.add(i);
        }

        while (!small.isEmpty() && !large.isEmpty()) {
            int less = small.removeLast();
            int more = large.removeLast();

            probability[less] = probabilities.get(less) * probabilities.size();
            alias[less] = more;

            probabilities.set(more,
                    (probabilities.get(more) + probabilities.get(less)) - average);

            if (probabilities.get(more) >= 1.0 / probabilities.size())
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
        return items.get(coinToss ? column : alias[column]);
    }

    public int size() {
        return items.size();
    }
}
