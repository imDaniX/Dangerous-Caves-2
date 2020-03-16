package com.github.evillootlye.caves.generator;

import com.github.evillootlye.caves.generator.defaults.BouldersGroup;
import com.github.evillootlye.caves.generator.defaults.BuildingsGroup;
import com.github.evillootlye.caves.generator.defaults.PillarsGroup;
import com.github.evillootlye.caves.generator.defaults.TrapsGroup;

public enum DefaultStructures {
    BOULDERS(new BouldersGroup()),
    BUILDINGS(new BuildingsGroup()),
    PILLARS(new PillarsGroup()),
    TRAPS(new TrapsGroup());

    private final StructureGroup group;

    DefaultStructures(StructureGroup group) {
        this.group = group;
    }

    public static void registerAll(CaveGenerator generator) {
        for(DefaultStructures struct : DefaultStructures.values())
            generator.register(struct.group);
    }
}
