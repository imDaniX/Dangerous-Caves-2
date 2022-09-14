package me.imdanix.caves.util.random;

public class PseudoRandom {
    public static final PseudoRandom ZERO_PSEUDO_RANDOM = new PseudoRandom(null) {
        @Override
        public int next() {
            return 0;
        }
    };

    private final int[] randomValues;
    private int cur = 0;

    public PseudoRandom(int[] randomValues) {
        this.randomValues = randomValues;
    }

    public int next() {
        return randomValues[cur >= randomValues.length ? (cur = 0) : cur++];
    }
}
