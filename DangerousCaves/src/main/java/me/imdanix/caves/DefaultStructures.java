package me.imdanix.caves;

import me.imdanix.caves.generator.CaveGenerator;
import me.imdanix.caves.generator.StructureGroup;
import me.imdanix.caves.generator.defaults.BouldersGroup;
import me.imdanix.caves.generator.defaults.BuildingsGroup;
import me.imdanix.caves.generator.defaults.PillarsGroup;
import me.imdanix.caves.generator.defaults.TrapsGroup;

enum DefaultStructures {
    BOULDERS(new BouldersGroup()),
    BUILDINGS(new BuildingsGroup()),
    PILLARS(new PillarsGroup()),
    TRAPS(new TrapsGroup());

    private final StructureGroup group;

    DefaultStructures(StructureGroup group) {
        this.group = group;
    }

    public static void registerAll(CaveGenerator generator) {
        for (DefaultStructures struct : DefaultStructures.values())
            generator.register(struct.group);
    }
}
