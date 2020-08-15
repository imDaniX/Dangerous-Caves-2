/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    SMOKE_DEMON(new SmokeDemon()),
    CAVE_GOLEM(new CaveGolem());

    private final CustomMob custom;

    DefaultMobs(CustomMob mob) {
        this.custom = mob;
    }

    public static void registerAll(MobsManager manager) {
        for(DefaultMobs mob : DefaultMobs.values())
            manager.register(mob.custom);
    }
}
