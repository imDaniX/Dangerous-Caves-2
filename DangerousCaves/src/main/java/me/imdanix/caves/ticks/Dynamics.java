package me.imdanix.caves.ticks;

import me.imdanix.caves.util.Manager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Dynamics implements Manager<Tickable> {
    private final Map<TickLevel, List<Tickable>> tickables;

    public Dynamics(Plugin plugin) {
        tickables = new EnumMap<>(TickLevel.class);
        int offset = 0;
        for (TickLevel level : TickLevel.values()) {
            tickables.put(level, new ArrayList<>());
            Bukkit.getScheduler().runTaskTimer(
                    plugin,
                    () -> tick(level),
                    level.getTicks() + offset++,
                    level.getTicks()
            );
        }
    }

    @Override
    public boolean register(Tickable tick) {
        if (tick.getTickLevel() == null) return false;
        tickables.get(tick.getTickLevel()).add(tick);
        return true;
    }

    // TODO: Spread among ticks
    public void tick(TickLevel level) {
        for (Tickable tickable : tickables.get(level)) {
            tickable.tick();
        }
    }

}
