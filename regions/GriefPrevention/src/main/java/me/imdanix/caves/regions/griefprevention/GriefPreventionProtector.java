package me.imdanix.caves.regions.griefprevention;

import me.imdanix.caves.regions.ActionType;
import me.imdanix.caves.regions.RegionProtector;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

public class GriefPreventionProtector implements RegionProtector {

    @Override
    public String getName() {
        return "griefprevention";
    }

    @Override
    public boolean test(ActionType actionType, Location location) {
        return GriefPrevention.instance.dataStore.getClaimAt(location, false, null) == null;
    }
}
