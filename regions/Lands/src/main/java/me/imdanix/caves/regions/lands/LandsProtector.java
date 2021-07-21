package me.imdanix.caves.regions.lands;

import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.enums.LandSetting;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.RegionProtector;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LandsProtector implements RegionProtector {
    private static LandsIntegration landsIntegration;
    private final boolean effect;

    public LandsProtector(boolean effect) {
        this.effect = effect;
    }

    @Override
    public void onEnable() {
        if (landsIntegration != null) return;
        landsIntegration = new LandsIntegration(Bukkit.getPluginManager().getPlugin("DangerousCaves"));
    }

    @Override
    public String getName() {
        return effect ? "lands" : "lands-effectless";
    }

    @Override
    public boolean test(CheckType type, Location location) {
        Area area = landsIntegration.getAreaByLoc(location);

        if (area == null) {
            return true;
        }

        switch (type) {
            case ENTITY -> {
                return area.hasLandSetting(LandSetting.ENTITY_GRIEFING);
            }
            case BLOCK -> {
                return area.hasLandSetting(LandSetting.LEAF_DECAY);
            }
            case EFFECT -> {
                return effect;
            }
            default -> {
                return false;
            }
        }
    }
}
