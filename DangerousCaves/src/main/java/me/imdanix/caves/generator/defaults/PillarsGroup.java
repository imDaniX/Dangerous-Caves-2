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

package me.imdanix.caves.generator.defaults;

import me.imdanix.caves.compatibility.VMaterial;
import me.imdanix.caves.generator.AbstractStructure;
import me.imdanix.caves.util.Locations;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

public class PillarsGroup extends AbstractStructure {
    private int weight;

    public PillarsGroup() {
        super("pillars");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("pillars", 1);
    }

    @Override
    public void generate(Random random, Chunk chunk, Block block) {
        Location loc = block.getLocation();
        switch (random.nextInt(8)) {
            case 1:
                setType(loc, VMaterial.POLISHED_ANDESITE.get());
                setType(loc.add(0, 1, 0), randomStone(random));
                setType(loc.add(0, 1, 0), VMaterial.STONE_BRICK_SLAB.get());
                break;

            case 2:
                setType(loc, VMaterial.STONE_BRICKS.get());
                if (random.nextBoolean()) {
                    setType(loc, VMaterial.CRACKED_STONE_BRICKS.get());
                }
                setType(loc.add(0, 1, 0), VMaterial.STONE_BRICKS.get());
                if (random.nextBoolean()) {
                    setType(loc, VMaterial.CRACKED_STONE_BRICKS.get());
                }
                setType(Locations.add(loc, random.nextInt(2), 0, 1), VMaterial.STONE_BRICK_SLAB.get());
                setType(loc.add(0, 1, 0), VMaterial.STONE_BRICK_SLAB.get());
                break;

            case 3:
                setType(loc, random.nextBoolean() ? VMaterial.STONE_BRICKS.get() : VMaterial.CRACKED_STONE_BRICKS.get());
                setType(loc.add(0, 1, 0), random.nextBoolean() ? VMaterial.STONE_BRICKS.get() : VMaterial.CRACKED_STONE_BRICKS.get());
                setType(loc.add(0, 1, 0), random.nextBoolean() ? VMaterial.STONE_BRICKS.get() : VMaterial.CRACKED_STONE_BRICKS.get());
                setType(loc.add(1, 0, random.nextInt(2)), VMaterial.STONE_BRICK_SLAB.get());
                break;

            case 4:
                setType(loc, randomStone(random));
                setType(Locations.add(loc, random.nextInt(2), 0, 1), VMaterial.STONE_BRICK_SLAB.get());
                setType(loc.add(0,1,0), VMaterial.POLISHED_ANDESITE.get());
                setType(loc.add(-1, 0, random.nextInt(2)), VMaterial.STONE_BRICK_SLAB.get());
                break;

            case 5:
                setType(loc, VMaterial.STONE_BRICKS.get());
                setType(loc.add(0, 1, 0), random.nextBoolean() ? VMaterial.STONE_BRICKS.get() : VMaterial.CRACKED_STONE_BRICKS.get());
                setType(loc.add(0, 1, 0), random.nextBoolean() ? VMaterial.STONE_BRICKS.get() : VMaterial.CRACKED_STONE_BRICKS.get());
                setType(Locations.add(loc, 0, 0, 1), VMaterial.STONE_BRICK_SLAB.get());
                setType(loc.add(0, 1, 0), random.nextBoolean() ? VMaterial.STONE_BRICKS.get() : VMaterial.CRACKED_STONE_BRICKS.get());
                setType(Locations.subtract(loc, 0 , 0, 1), VMaterial.STONE_BRICK_SLAB.get());
                setType(loc.add(0, 1, 0), VMaterial.STONE_BRICK_SLAB.get());
                break;

            case 6:
                setType(loc, VMaterial.STONE_BRICKS.get());
                setType(loc.add(0, 1, 0), random.nextBoolean() ? VMaterial.STONE_BRICKS.get() : VMaterial.CRACKED_STONE_BRICKS.get());
                setType(Locations.add(loc, random.nextInt(2), 0, 1), VMaterial.STONE_BRICK_SLAB.get());
                setType(loc.add(0, 1, 0), random.nextBoolean() ? VMaterial.STONE_BRICKS.get() : VMaterial.CRACKED_STONE_BRICKS.get());
                setType(Locations.add(loc, 1, 0, 0), VMaterial.STONE_BRICK_SLAB.get());
                setType(loc.add(0, 1, 0), random.nextBoolean() ? VMaterial.STONE_BRICKS.get() : VMaterial.CRACKED_STONE_BRICKS.get());
                setType(loc.add(0, 1, 0), random.nextBoolean() ? VMaterial.STONE_BRICKS.get() : VMaterial.CRACKED_STONE_BRICKS.get());
                setType(Locations.add(loc, 1, 0 ,0), VMaterial.STONE_BRICK_SLAB.get());
                setType(loc.add(0, 1, 0), random.nextBoolean() ? VMaterial.STONE_BRICKS.get() : VMaterial.CRACKED_STONE_BRICKS.get());
                setType(Locations.subtract(loc, 1, 0, 0), VMaterial.STONE_BRICK_SLAB.get());
                setType(loc.add(0, 1, 0), VMaterial.STONE_BRICK_SLAB.get());
                setType(loc.add(1, 0, 0), VMaterial.STONE_BRICK_SLAB.get());
                break;

            case 7:
                setType(loc, Material.COBBLESTONE);
                setType(loc.add(0, 1, 0), VMaterial.COBBLESTONE_WALL.get());
                break;

            default:
                setType(loc, VMaterial.POLISHED_ANDESITE.get());
                setType(loc.add(0, 1, 0), VMaterial.STONE_BRICK_SLAB.get());
                break;
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
