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
