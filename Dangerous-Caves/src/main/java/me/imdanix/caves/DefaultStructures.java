/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
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

package me.imdanix.caves;

import me.imdanix.caves.generator.AbstractStructure;
import me.imdanix.caves.generator.CaveGenerator;
import me.imdanix.caves.generator.defaults.BouldersGroup;
import me.imdanix.caves.generator.defaults.BuildingsGroup;
import me.imdanix.caves.generator.defaults.PillarsGroup;
import me.imdanix.caves.generator.defaults.TrapsGroup;

public enum DefaultStructures {
    BOULDERS(new BouldersGroup()),
    BUILDINGS(new BuildingsGroup()),
    PILLARS(new PillarsGroup()),
    TRAPS(new TrapsGroup());

    private final AbstractStructure group;

    DefaultStructures(AbstractStructure group) {
        this.group = group;
    }

    public static void registerAll(CaveGenerator generator) {
        for(DefaultStructures struct : DefaultStructures.values())
            generator.register(struct.group);
    }
}
