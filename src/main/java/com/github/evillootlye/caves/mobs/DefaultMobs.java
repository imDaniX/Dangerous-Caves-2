package com.github.evillootlye.caves.mobs;

enum DefaultMobs {
    ALPHA_SPIDER(new AlphaSpider()), HEXED_ARMOR(new HexedArmor());
    private final CustomMob mob;

    DefaultMobs(CustomMob mob) {
        this.mob = mob;
    }

    public CustomMob getMob() {
        return mob;
    }
}
