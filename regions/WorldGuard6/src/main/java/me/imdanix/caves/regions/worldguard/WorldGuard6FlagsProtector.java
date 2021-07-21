package me.imdanix.caves.regions.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.RegionProtector;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class WorldGuard6FlagsProtector implements RegionProtector {
    private static final StateFlag ENTITY_FLAG = new StateFlag("dc-entity-grief", true);
    private static final StateFlag BLOCK_FLAG = new StateFlag("dc-block-change", true);
    private static final StateFlag EFFECT_FLAG = new StateFlag("dc-player-effect", true);

    private final WorldGuardPlugin worldGuard;

    public WorldGuard6FlagsProtector() {
        worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        FlagRegistry registry = worldGuard.getFlagRegistry();
        try {
            registry.register(ENTITY_FLAG);
            registry.register(BLOCK_FLAG);
            registry.register(EFFECT_FLAG);
        } catch (FlagConflictException ignored) {}
    }

    @Override
    public String getName() {
        return "worldguard-flags";
    }

    @Override
    public boolean test(CheckType type, Location location) {
        ApplicableRegionSet set = worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location);
        return switch (type) {
            case ENTITY -> set.testState(null, ENTITY_FLAG);
            case BLOCK -> set.testState(null, BLOCK_FLAG);
            case EFFECT -> set.testState(null, EFFECT_FLAG);
        };
    }
}
