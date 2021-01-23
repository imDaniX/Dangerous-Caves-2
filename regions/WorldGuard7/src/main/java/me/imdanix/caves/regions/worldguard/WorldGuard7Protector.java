package me.imdanix.caves.regions.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.RegionProtector;
import org.bukkit.Location;

public class WorldGuard7Protector implements RegionProtector {
    @Override
    public boolean test(CheckType checkType, Location location) {
        ApplicableRegionSet set = getContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
        return set.size() == 0;
    }

    @Override
    public String getName() {
        return "worldguard";
    }

    private RegionContainer getContainer() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }
}
