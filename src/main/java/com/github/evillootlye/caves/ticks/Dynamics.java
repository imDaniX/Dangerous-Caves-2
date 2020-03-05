package com.github.evillootlye.caves.ticks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Dynamics {
    private final Map<TickLevel, Set<Tickable>> tickables;

    public Dynamics(Plugin plugin) {
        tickables = new EnumMap<>(TickLevel.class);
        for(TickLevel level : TickLevel.values()) {
            tickables.put(level, new HashSet<>());
            Bukkit.getScheduler().scheduleSyncRepeatingTask(
                    plugin,
                    () -> tickables.get(level).forEach(Tickable::tick),
                    level.ticks,
                    level.ticks
            );
        }
    }

    public void subscribe(Tickable tick) {
        tickables.get(tick.getTickLevel()).add(tick);
    }

    public enum TickLevel {
        WORLD(6300), ENTITY(4);

        public final int ticks;

        TickLevel(int ticks) {
            this.ticks = ticks;
        }
    }

}
