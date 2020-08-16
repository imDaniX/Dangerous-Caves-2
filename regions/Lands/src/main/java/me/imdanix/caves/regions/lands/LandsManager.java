package me.imdanix.caves.regions.lands;

import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.enums.LandSetting;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LandsManager implements RegionManager {
    private LandsIntegration landsIntegration;
    private final boolean effect;

    public LandsManager(boolean effect) {
        this.effect = effect;
    }

    @Override
    public void onEnable() {
        this.landsIntegration = new LandsIntegration(Bukkit.getPluginManager().getPlugin("DangerousCaves"));
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
            case ENTITY: {
                return area.hasLandSetting(LandSetting.ENTITY_GRIEFING);
            }
            case BLOCK: {
                return area.hasLandSetting(LandSetting.LEAF_DECAY);
            }
            case EFFECT: {
                return effect;
            }
            default: {
                return false;
            }
        }
    }
}
