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

import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.compatibility.VMaterial;
import me.imdanix.caves.generator.AbstractStructure;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TrapsGroup extends AbstractStructure {
    private int weight;

    public TrapsGroup() {
        super("traps");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("traps", 1);
    }

    @Override
    public void generate(Random rnd, Chunk chunk, Block start) {
        Location loc = start.getLocation();
        switch (rnd.nextInt(9)) {
            case 1:
                while(loc.getY()>4) {
                    setType(loc.subtract(0, 1, 0), Material.AIR);
                }
                setType(loc.add(0, 1, 0), Material.LAVA);
                setType(loc.add(0, 1, 0), Material.LAVA);
                setType(loc.add(0, 1, 0), Material.LAVA);
                break;

            case 2:
                setType(loc, VMaterial.STONE_PRESSURE_PLATE.get());
                setType(loc.subtract(0, 1, 0), Material.GRAVEL);
                setType(loc.subtract(0, 1, 0), Material.TNT);
                while(loc.getY()>4) {
                    setType(loc.subtract(0, 1, 0), Material.AIR);
                }
                setType(loc.add(0, 1, 0), Material.LAVA);
                setType(loc.add(0, 1, 0), Material.LAVA);
                setType(loc.add(0, 1, 0), Material.LAVA);
                break;

            case 3:
                setType(loc, VMaterial.STONE_PRESSURE_PLATE.get());
                setType(loc.subtract(0, 1, 0), Material.GRAVEL);
                setType(loc.subtract(0, 1, 0), Material.TNT);
                while(loc.getY()>4) {
                    setType(loc.subtract(0, 1, 0), Material.AIR);
                }
                setType(loc.add(0, 1, 0), VMaterial.COBWEB.get());
                setType(loc.add(0, 1, 0), VMaterial.COBWEB.get());
                setType(loc.add(0, 1, 0), VMaterial.COBWEB.get());
                break;

            case 4:
                setType(loc, VMaterial.STONE_PRESSURE_PLATE.get());
                setType(loc.subtract(0, 1, 0), Material.GRAVEL);
                setType(loc.subtract(0, 1, 0), Material.TNT);
                while(loc.getY()>4) {
                    setType(loc.subtract(0, 1, 0), Material.AIR);
                }
                break;

            case 5:
                setType(loc, VMaterial.STONE_PRESSURE_PLATE.get());
                setType(loc.subtract(0, 1, 0), Material.GRAVEL);
                setType(loc.subtract(0, 1, 0), Material.TNT);
                if(rnd.nextBoolean()) {
                    setType(loc.add(0, 0, 1), Material.TNT);
                    if(rnd.nextBoolean()) {
                        setType(loc.add(1, 0, 0), Material.TNT);
                    }
                }
                break;

            case 6:
                setType(loc, VMaterial.STONE_PRESSURE_PLATE.get());
                setType(loc.subtract(0, 2, 0), Material.TNT);
                if(rnd.nextBoolean()) {
                    setType(loc.add(0, 0, 1), Material.TNT);
                    if(rnd.nextBoolean()) {
                        setType(loc.add(1, 0, 0), Material.TNT);
                    }
                }
                break;

            case 7:
                setType(loc, Material.TRAPPED_CHEST);
                fillInventory(loc.getBlock());
                setType(loc.subtract(0, 2, 0), Material.TNT);
                if(rnd.nextBoolean()) {
                    setType(loc.add(0, 0, 1), Material.TNT);
                    if(rnd.nextBoolean()) {
                        setType(loc.add(1, 0, 0), Material.TNT);
                    }
                }
                break;

            case 8:
                setType(loc, VMaterial.STONE_PRESSURE_PLATE.get());
                setType(loc.subtract(0, 1, 0), Material.DISPENSER);
                Compatibility.rotate(loc.getBlock(), BlockFace.UP);
                ((Dispenser) loc.getBlock().getState()).getInventory().addItem(new ItemStack(Material.ARROW, 3));
                break;

            default:
                while(loc.getY() > 4)
                    setType(loc.subtract(0, 1, 0), Material.AIR);
                break;
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
