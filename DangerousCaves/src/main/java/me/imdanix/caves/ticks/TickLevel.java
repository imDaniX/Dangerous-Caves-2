package me.imdanix.caves.ticks;

// TODO: Configurable?
public enum TickLevel {
    WORLD(6300), PLAYER(800), ENTITY(4);

    private final int ticks;

    TickLevel(int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
