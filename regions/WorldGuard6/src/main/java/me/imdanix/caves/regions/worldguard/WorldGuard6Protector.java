package me.imdanix.caves.regions.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.RegionProtector;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class WorldGuard6Protector implements RegionProtector {
    private final WorldGuardPlugin worldGuard;

    public WorldGuard6Protector() {
        worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    }

    @Override
    public String getName() {
        return "worldguard";
    }

    @Override
    public boolean test(CheckType checkType, Location location) {
        ApplicableRegionSet set = worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location);
        return set.size() == 0;
    }
}
