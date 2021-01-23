package me.imdanix.caves.ticks;

import me.imdanix.caves.Manager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Dynamics implements Manager<Tickable> {
    private final Map<TickLevel, Set<Tickable>> tickables;

    public Dynamics(Plugin plugin) {
        tickables = new EnumMap<>(TickLevel.class);
        int offset = 0;
        for (TickLevel level : TickLevel.values()) {
            tickables.put(level, new HashSet<>());
            Bukkit.getScheduler().scheduleSyncRepeatingTask(
                    plugin,
                    () -> tick(level),
                    level.ticks + offset++,
                    level.ticks
            );
        }
    }

    @Override
    public boolean register(Tickable tick) {
        if (tick.getTickLevel() == null) return false;
        tickables.get(tick.getTickLevel()).add(tick);
        return true;
    }

    public void tick(TickLevel level) {
        for (Tickable tickable : tickables.get(level))
            tickable.tick();
    }

}
