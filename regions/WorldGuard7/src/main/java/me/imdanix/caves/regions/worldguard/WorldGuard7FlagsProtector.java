package me.imdanix.caves.regions.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.imdanix.caves.regions.ActionType;
import me.imdanix.caves.regions.RegionProtector;
import org.bukkit.Location;

public class WorldGuard7FlagsProtector implements RegionProtector {
    private static final StateFlag ENTITY_FLAG = new StateFlag("dc-entity-grief", true);
    private static final StateFlag BLOCK_FLAG = new StateFlag("dc-block-change", true);
    private static final StateFlag EFFECT_FLAG = new StateFlag("dc-player-effect", true);

    public WorldGuard7FlagsProtector() {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
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
    public boolean test(ActionType type, Location location) {
        ApplicableRegionSet set = getContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
        return switch (type) {
            case ENTITY -> set.testState(null, ENTITY_FLAG);
            case BLOCK -> set.testState(null, BLOCK_FLAG);
            case EFFECT -> set.testState(null, EFFECT_FLAG);
        };
    }

    private RegionContainer getContainer() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }
}
