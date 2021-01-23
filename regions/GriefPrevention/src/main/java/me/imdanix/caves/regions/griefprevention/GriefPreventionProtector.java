package me.imdanix.caves.regions.griefprevention;

import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.RegionProtector;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

public class GriefPreventionProtector implements RegionProtector {

    @Override
    public String getName() {
        return "griefprevention";
    }

    @Override
    public boolean test(CheckType checkType, Location location) {
        return GriefPrevention.instance.dataStore.getClaimAt(location, false, null) == null;
    }
}
