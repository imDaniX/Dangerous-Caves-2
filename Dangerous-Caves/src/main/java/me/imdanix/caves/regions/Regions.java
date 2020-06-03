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

package me.imdanix.caves.regions;

import me.imdanix.caves.configuration.Configurable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public enum Regions implements Configurable {
    INST;

    private final Map<String, RegionManager> managers;
    private BiPredicate<CheckType, Location> current;

    Regions() {
        managers = new HashMap<>();
    }

    public boolean check(CheckType check, Location location) {
        return current.test(check, location);
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        boolean invert = cfg.getBoolean("invert", false);
        String mode = cfg.getString("mode", "none").toLowerCase();
        RegionManager manager = managers.get(mode);
        if(manager != null) {
            current = (c,l) -> invert ^ manager.test(c, l);
        } else
            current = (c,l) -> true;
    }

    @Override
    public String getPath() {
        return "integration";
    }

    public void onLoad() {
        Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if(wg != null) {
            if(wg.getDescription().getVersion().startsWith("6"))
                managers.put("worldguard", new WorldGuard6Manager());
            else
                managers.put("worldguard", new WorldGuard7Manager());
        }
    }

    public void onEnable() {
        managers.put("griefprevention", new GriefPreventionManager());

        managers.values().forEach(RegionManager::onEnable);
    }
}
