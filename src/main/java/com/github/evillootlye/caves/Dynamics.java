package com.github.evillootlye.caves;

import org.bukkit.Bukkit;

import java.util.EnumMap;
import java.util.Map;

public class Dynamics {
    private final Map<TickLevel, Tickable> tickables;

    public Dynamics() {
        tickables = new EnumMap<>(TickLevel.class);
        for(TickLevel level : TickLevel.values())
            Bukkit.getScheduler().scheduleSyncRepeatingTask(
                    DangerousCaves.INSTANCE,
                    () -> tickables.get(level).tick(),
                    level.ticks,
                    level.ticks
            );
    }

    public void subscribe(Tickable tick) {
        tickables.put(tick.getTickLevel(), tick);
    }

    public enum TickLevel {
        WORLD(6000), ENTITY(4);

        public final int ticks;

        TickLevel(int ticks) {
            this.ticks = ticks;
        }
    }

    public interface Tickable {
        void tick();
        TickLevel getTickLevel();
    }
}
