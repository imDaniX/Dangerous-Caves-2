package com.github.evillootlye.caves.ticks;

public interface Tickable {
    void tick();
    Dynamics.TickLevel getTickLevel();
}
