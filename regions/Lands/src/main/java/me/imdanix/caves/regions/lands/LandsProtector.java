/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2021  imDaniX
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
