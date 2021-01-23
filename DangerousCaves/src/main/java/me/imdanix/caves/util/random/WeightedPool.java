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
