package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.mobs.CustomMob;
import me.imdanix.caves.mobs.MobsManager;

import java.util.function.Function;

public enum DefaultMobs {
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
