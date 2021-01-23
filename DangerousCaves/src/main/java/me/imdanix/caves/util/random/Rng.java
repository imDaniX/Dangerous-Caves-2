package me.imdanix.caves.util.random;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Rng {

    public static <T> T randomElement(List<T> list) {
        return list.get(Rng.nextInt(list.size()));
    }

    public static boolean chance(double v) {
        return nextDouble() < v;
    }

    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static int nextInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static int nextInt(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    public static int nextInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static double nextDouble(double max) {
        return ThreadLocalRandom.current().nextDouble(max);
    }

    public static double nextDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}
