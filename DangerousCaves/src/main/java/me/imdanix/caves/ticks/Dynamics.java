/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    @Override
    public boolean register(Tickable tick) {
        if(tick.getTickLevel() == null) return false;
        tickables.get(tick.getTickLevel()).add(tick);
        return true;
    }

    public void tick(TickLevel level) {
        for(Tickable tickable : tickables.get(level))
            tickable.tick();
    }

}
