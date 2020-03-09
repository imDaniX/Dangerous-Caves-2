package com.github.evillootlye.caves.mobs;

import com.github.evillootlye.caves.mobs.defaults.AlphaSpider;
import com.github.evillootlye.caves.mobs.defaults.CryingBat;
import com.github.evillootlye.caves.mobs.defaults.DeadMiner;
import com.github.evillootlye.caves.mobs.defaults.HexedArmor;
import com.github.evillootlye.caves.mobs.defaults.HungeringDarkness;
import com.github.evillootlye.caves.mobs.defaults.LavaCreeper;
import com.github.evillootlye.caves.mobs.defaults.MagmaMonster;
import com.github.evillootlye.caves.mobs.defaults.TNTCreeper;
import com.github.evillootlye.caves.mobs.defaults.Watcher;

public enum DefaultMobs {
    ALPHA_SPIDER(new AlphaSpider()),
    HEXED_ARMOR(new HexedArmor()),
    MAGMA_MONSTER(new MagmaMonster()),
    HUNGERING_DARKNESS(new HungeringDarkness()),
    CRYING_BAT(new CryingBat()),
    WATCHER(new Watcher()),
    TNT_CREEPER(new TNTCreeper()),
    LAVA_CREEPER(new LavaCreeper()),
    DEAD_MINER(new DeadMiner());

    private final CustomMob custom;

    DefaultMobs(CustomMob mob) {
        this.custom = mob;
    }

    public static void registerAll(MobsManager manager) {
        for(DefaultMobs mob : DefaultMobs.values())
            manager.register(mob.custom);
    }
}
