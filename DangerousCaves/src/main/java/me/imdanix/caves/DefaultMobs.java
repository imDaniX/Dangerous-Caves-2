package me.imdanix.caves;

import me.imdanix.caves.mobs.CustomMob;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.mobs.defaults.*;

import java.util.function.Function;

enum DefaultMobs {
    MIMIC(Mimic::new),
    CAVE_GOLEM(CaveGolem::new),
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

    private final Function<MobsManager, CustomMob> custom;

    DefaultMobs(CustomMob mob) {
        this.custom = mm -> mob;
    }

    DefaultMobs(Function<MobsManager, CustomMob> custom) {
        this.custom = custom;
    }

    public static void registerAll(MobsManager manager) {
        for (DefaultMobs mob : DefaultMobs.values()) {
            CustomMob customMob = mob.custom.apply(manager);
            manager.register(customMob);
        }
    }
}
