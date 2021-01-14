/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2021  imDaniX
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

package me.imdanix.caves.generator.defaults;

import me.imdanix.caves.generator.AbstractStructure;
import me.imdanix.caves.util.Locations;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

public class BouldersGroup extends AbstractStructure {
    private int weight;

    public BouldersGroup() {
        super("boulders");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("boulders", 1);
    }

    @Override
    public void generate(Random random, Chunk chunk, Block block) {
        Location loc = block.getLocation();
        boolean[][][] rock;
        switch (random.nextInt(8)) {
            default:rock = OldStructures.rock1; break;
            case 1: rock = OldStructures.rock2; break;
            case 2: rock = OldStructures.rock3; break;
            case 3: rock = OldStructures.rock4; break;
            case 4: rock = OldStructures.rock5; break;
            case 5: rock = OldStructures.rock6; break;
            case 6: rock = OldStructures.rock7; break;
            case 7: rock = OldStructures.rock8; break;
        }
        int xMod = random.nextBoolean() ? -1 : 1;
        int zMod = random.nextBoolean() ? -1 : 1;
        for (int y = 0; y < rock[0].length; y++) for (int x = -1; x < rock.length-1; x++) for (int z = -1; z < rock[0][0].length-1; z++) {
            if (rock[x + 1][y][z + 1]) {
                setType(Locations.add(loc, x * xMod, y, z * zMod), randomStone(random));
            }
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
