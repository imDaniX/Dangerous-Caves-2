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

import me.imdanix.caves.Manager;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.regions.griefprevention.GriefPreventionFlagsManager;
import me.imdanix.caves.regions.griefprevention.GriefPreventionManager;
import me.imdanix.caves.regions.lands.LandsManager;
import me.imdanix.caves.regions.worldguard.WorldGuard6FlagsManager;
import me.imdanix.caves.regions.worldguard.WorldGuard6Manager;
import me.imdanix.caves.regions.worldguard.WorldGuard7FlagsManager;
import me.imdanix.caves.regions.worldguard.WorldGuard7Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public enum Regions implements Manager<RegionManager>, Configurable {
    INSTANCE;
    private final RegionManager NONE = (c, l) -> true;

    private final Map<String, RegionManager> managers;
    private final List<RegionManager> current;

    Regions() {
        managers = new HashMap<>();
        managers.put("none", NONE);
        current = new ArrayList<>();
    }

    public boolean check(CheckType check, Location location) {
        for(RegionManager regions : current) {
            if(!regions.test(check, location))
                return false;
        }
        return true;
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        // TODO Logger util
        Logger logger = Bukkit.getPluginManager().getPlugin("DangerousCaves").getLogger();

        boolean invert;
        String[] modes;
        if(cfg.isString("protection.mode")) {
            invert = cfg.getBoolean("protection.invert", false);
            modes = cfg.getString("protection.mode", "none").toLowerCase(Locale.ENGLISH).split(",\\s*");
        } else {
            invert = cfg.getBoolean("invert", false);
            modes = cfg.getString("mode", "none").toLowerCase(Locale.ENGLISH).split(",\\s*");
            logger.warning("Please check latest changes in the config regarding WG and GP protections. It should " +
                    "be moved into a new section of the config. It will no longer work in the next update.");
        }

        if(modes.length == 0) {
            current.add(NONE);
        } else for(String mode : modes) {
            RegionManager manager = managers.get(mode);
            if(manager != null) {
                current.add((c,l) -> invert != manager.test(c, l));
            } else {
                logger.warning("Can't find mode \"" + mode + "\".");
            }
        }
    }

    @Override
    public String getPath() {
        return "integration";
    }

    public void onLoad() {
        Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if(wg != null) {
            if(wg.getDescription().getVersion().startsWith("6")) {
                register(new WorldGuard6FlagsManager());
                register(new WorldGuard6Manager());
            } else {
                register(new WorldGuard7FlagsManager());
                register(new WorldGuard7Manager());
            }
        }
    }

    public void onEnable() {
        if(Bukkit.getPluginManager().isPluginEnabled("GriefPrevention")) {
            register(new GriefPreventionManager());
            if(Bukkit.getPluginManager().isPluginEnabled("GriefPreventionFlags"))
                register( new GriefPreventionFlagsManager());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Lands")) {
            register(new LandsManager(true));
            register(new LandsManager(false));
        }

        managers.values().forEach(RegionManager::onEnable);
    }

    @Override
    public boolean register(RegionManager regionManager) {
        if(!managers.containsKey(regionManager.getName())) {
            managers.put(regionManager.getName(), regionManager);
            return true;
        }
        return false;
    }
}
