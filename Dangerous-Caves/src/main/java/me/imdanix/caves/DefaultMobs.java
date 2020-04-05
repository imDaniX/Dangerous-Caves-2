package me.imdanix.caves;

import me.imdanix.caves.mobs.CustomMob;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.mobs.defaults.AlphaSpider;
import me.imdanix.caves.mobs.defaults.CryingBat;
import me.imdanix.caves.mobs.defaults.DeadMiner;
import me.imdanix.caves.mobs.defaults.HexedArmor;
import me.imdanix.caves.mobs.defaults.HungeringDarkness;
import me.imdanix.caves.mobs.defaults.LavaCreeper;
import me.imdanix.caves.mobs.defaults.MagmaMonster;
import me.imdanix.caves.mobs.defaults.SmokeDemon;
import me.imdanix.caves.mobs.defaults.TNTCreeper;
import me.imdanix.caves.mobs.defaults.Watcher;

public enum DefaultMobs {
    ALPHA_SPIDER(new AlphaSpider()),
    HEXED_ARMOR(new HexedArmor()),
    MAGMA_MONSTER(new MagmaMonster()),
    HUNGERING_DARKNESS(new HungeringDarkness()),
    CRYING_BAT(new CryingBat()),
    WATCHER(new Watcher()),
    TNT_CREEPER(new TNTCreeper()),
    LAVA_CREEPER(new LavaCreeper()),
    DEAD_MINER(new DeadMiner()),
    SMOKE_DEMON(new SmokeDemon());

    private final CustomMob custom;

    DefaultMobs(CustomMob mob) {
        this.custom = mob;
    }

    public static void registerAll(MobsManager manager) {
        for(DefaultMobs mob : DefaultMobs.values())
            manager.register(mob.custom);
    }
}
