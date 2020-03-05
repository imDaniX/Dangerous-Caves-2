package com.github.evillootlye.caves.mobs;

import com.github.evillootlye.caves.mobs.defaults.AlphaSpider;
import com.github.evillootlye.caves.mobs.defaults.HexedArmor;
import com.github.evillootlye.caves.mobs.defaults.HungeringDarkness;
import com.github.evillootlye.caves.mobs.defaults.MagmaMonster;

public enum DefaultMobs {
    ALPHA_SPIDER(new AlphaSpider()),
    HEXED_ARMOR(new HexedArmor()),
    MAGMA_MONSTER(new MagmaMonster()),
    HUNGERING_DARKNESS(new HungeringDarkness());

    private final CustomMob custom;

    DefaultMobs(CustomMob mob) {
        this.custom = mob;
    }

    public static void registerAll(MobsManager manager) {
        for(DefaultMobs mob : DefaultMobs.values())
            manager.register(mob.custom);
    }
}
