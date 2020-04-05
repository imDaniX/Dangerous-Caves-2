package me.imdanix.caves.ticks;

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
                    () -> tick(level),
                    level.ticks,
                    level.ticks
            );
        }
    }

    public void subscribe(Tickable tick) {
        tickables.get(tick.getTickLevel()).add(tick);
    }

    public void tick(TickLevel level) {
        tickables.get(level).forEach(Tickable::tick);
    }

}
