package com.github.evillootlye.caves.mobs;

import org.bukkit.Location;

public enum DefaultMobs {
    ALPHA_SPIDER(new AlphaSpider()), HEXED_ARMOR(new HexedArmor());
    private final CustomMob mob;

    DefaultMobs(CustomMob mob) {
        this.mob = mob;
    }

    public CustomMob getMob() {
        return mob;
    }

    public void spawn(Location location) {
        mob.spawn(location);
    }
}
