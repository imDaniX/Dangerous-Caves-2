package com.github.evillootlye.caves.utils.random;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
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

    /* The probability and alias tables. */
    private final int[] alias;
    private final double[] probability;

    private final List<T> items;

    /**
     * Constructs a new AliasMethod to sample from a discrete distribution and
     * hand back outcomes based on the probability distribution.
     * <p>
     * Given as input a list of probabilities corresponding to outcomes 0, 1,
     * ..., n - 1, this constructor creates the probability and alias tables
     * needed to efficiently sample from this distribution.
     *
     * @param collection The list of probabilities.
     */
    public AliasMethod(Collection<T> collection, ToDoubleFunction<T> funct) {
        /* Begin by doing basic structural checks on the inputs. */
        if (collection == null || funct == null)
            throw new NullPointerException();
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

        /* Allocate space for the probability and alias tables. */
        probability = new double[probabilities.size()];
        alias = new int[probabilities.size()];

        /* Compute the average probability and cache it for later use. */
        final double average = 1.0 / probabilities.size();

        /* Create two stacks to act as worklists as we populate the tables. */
        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> large = new ArrayDeque<>();

        /* Populate the stacks with the input probabilities. */
        for (int i = 0; i < probabilities.size(); ++i) {
            /* If the probability is below the average probability, then we add
             * it to the small list; otherwise we add it to the large list.
             */
            if (probabilities.get(i) >= average)
                large.add(i);
            else
                small.add(i);
        }

        /* As a note: in the mathematical specification of the algorithm, we
         * will always exhaust the small list before the big list.  However,
         * due to floating point inaccuracies, this is not necessarily true.
         * Consequently, this inner loop (which tries to pair small and large
         * elements) will have to check that both lists aren't empty.
         */
        while (!small.isEmpty() && !large.isEmpty()) {
            /* Get the index of the small and the large probabilities. */
            int less = small.removeLast();
            int more = large.removeLast();

            /* These probabilities have not yet been scaled up to be such that
             * 1/n is given weight 1.0.  We do this here instead.
             */
            probability[less] = probabilities.get(less) * probabilities.size();
            alias[less] = more;

            /* Decrease the probability of the larger one by the appropriate
             * amount.
             */
            probabilities.set(more,
                    (probabilities.get(more) + probabilities.get(less)) - average);

            /* If the new probability is less than the average, add it into the
             * small list; otherwise add it to the large list.
             */
            if (probabilities.get(more) >= 1.0 / probabilities.size())
                large.add(more);
            else
                small.add(more);
        }

        /* At this point, everything is in one list, which means that the
         * remaining probabilities should all be 1/n.  Based on this, set them
         * appropriately.  Due to numerical issues, we can't be sure which
         * stack will hold the entries, so we empty both.
         */
        while (!small.isEmpty())
            probability[small.removeLast()] = 1.0;
        while (!large.isEmpty())
            probability[large.removeLast()] = 1.0;
    }

    /**
     * Samples a value from the underlying distribution.
     *
     * @return A random value sampled from the underlying distribution.
     */
    public T next() {
        /* Generate a fair die roll to determine which column to inspect. */
        int column = Rnd.nextInt(probability.length);

        /* Generate a biased coin toss to determine which option to pick. */
        boolean coinToss = Rnd.nextDouble() < probability[column];

        /* Based on the outcome, return either the column or its alias. */
        return items.get(coinToss ? column : alias[column]);
    }
}
