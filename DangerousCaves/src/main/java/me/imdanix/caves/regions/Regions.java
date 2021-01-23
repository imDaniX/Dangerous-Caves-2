package me.imdanix.caves.regions;

import me.imdanix.caves.Manager;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.regions.griefprevention.GriefPreventionFlagsProtector;
import me.imdanix.caves.regions.griefprevention.GriefPreventionProtector;
import me.imdanix.caves.regions.lands.LandsProtector;
import me.imdanix.caves.regions.worldguard.WorldGuard6FlagsProtector;
import me.imdanix.caves.regions.worldguard.WorldGuard6Protector;
import me.imdanix.caves.regions.worldguard.WorldGuard7FlagsProtector;
import me.imdanix.caves.regions.worldguard.WorldGuard7Protector;
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

public enum Regions implements Manager<RegionProtector>, Configurable {
    INSTANCE;
    private final RegionProtector NONE = (c, l) -> true;

    private final Map<String, RegionProtector> protectors;
    private final List<RegionProtector> current;

    Regions() {
        protectors = new HashMap<>();
        protectors.put("none", NONE);
        current = new ArrayList<>();
    }

    public boolean check(CheckType check, Location location) {
        for (RegionProtector regions : current) {
            if (!regions.test(check, location))
                return false;
        }
        return true;
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        // TODO Logger util
        Logger logger = Bukkit.getPluginManager().getPlugin("DangerousCaves").getLogger();

        boolean invert = cfg.getBoolean("invert", false);
        String[] modes = cfg.getString("mode", "none").toLowerCase(Locale.ENGLISH).split(",\\s*");

        List<String> failedModes = new ArrayList<>();
        if (modes.length == 0) {
            current.add(NONE);
        } else for (String mode : modes) {
            RegionProtector manager = protectors.get(mode);
            if (manager != null) {
                current.add((c,l) -> invert != manager.test(c, l));
            } else {
                failedModes.add(mode);
            }
        }
        if (failedModes.size() > 0) {
            String modeOrModes = failedModes.size() > 1 ? "modes(" : "mode(";
            logger.warning("Cannot find " + modeOrModes + String.join(", ", failedModes) + ") for regions protection.");
        }
    }

    @Override
    public String getConfigPath() {
        return "integration.protection";
    }

    public void onLoad() {
        Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (wg != null) {
            if (wg.getDescription().getVersion().startsWith("6")) {
                register(new WorldGuard6FlagsProtector());
                register(new WorldGuard6Protector());
            } else {
                register(new WorldGuard7FlagsProtector());
                register(new WorldGuard7Protector());
            }
        }
    }

    public void onEnable() {
        if (Bukkit.getPluginManager().isPluginEnabled("Lands")) {
            register(new LandsProtector(true));
            register(new LandsProtector(false));
        }

        protectors.values().forEach(RegionProtector::onEnable);
    }

    public void onDone() {
        if (Bukkit.getPluginManager().isPluginEnabled("GriefPrevention")) {
            register(new GriefPreventionProtector());
            if (Bukkit.getPluginManager().isPluginEnabled("GriefPreventionFlags"))
                register(new GriefPreventionFlagsProtector());
        }
    }

    @Override
    public boolean register(RegionProtector regionProtector) {
        if (!protectors.containsKey(regionProtector.getName())) {
            protectors.put(regionProtector.getName(), regionProtector);
            return true;
        }
        return false;
    }
}
