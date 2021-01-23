package me.imdanix.caves;

import me.imdanix.caves.mobs.CustomMob;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.mobs.defaults.AlphaSpider;
import me.imdanix.caves.mobs.defaults.CaveGolem;
import me.imdanix.caves.mobs.defaults.CryingBat;
import me.imdanix.caves.mobs.defaults.DeadMiner;
import me.imdanix.caves.mobs.defaults.HexedArmor;
import me.imdanix.caves.mobs.defaults.HungeringDarkness;
import me.imdanix.caves.mobs.defaults.LavaCreeper;
import me.imdanix.caves.mobs.defaults.MagmaMonster;
import me.imdanix.caves.mobs.defaults.Mimic;
import me.imdanix.caves.mobs.defaults.SmokeDemon;
import me.imdanix.caves.mobs.defaults.TNTCreeper;
import me.imdanix.caves.mobs.defaults.Watcher;

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
    private CustomMob mob;

    DefaultMobs(CustomMob mob) {
        this.custom = mm -> mob;
    }

    DefaultMobs(Function<MobsManager, CustomMob> custom) {
        this.custom = custom;
    }

    public CustomMob getMob() {
        return mob;
    }

    public static void registerAll(MobsManager manager) {
        for (DefaultMobs mob : DefaultMobs.values()) {
            CustomMob customMob = mob.custom.apply(manager);
            manager.register(customMob);
            mob.mob = customMob;
        }
    }
}
