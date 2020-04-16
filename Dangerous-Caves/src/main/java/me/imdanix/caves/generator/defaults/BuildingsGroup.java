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
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.generator.StructureGroup;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.Random;

@Configurable.Path("generator.structures")
public class BuildingsGroup extends StructureGroup implements Configurable {
    private int weight;

    public BuildingsGroup() {
        super("buildings");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("buildings");
    }

    @Override
    public void generate(Random random, Chunk chunk, Block block) {
        Location loc = block.getLocation();
        switch (random.nextInt(10)) {
            default:
                setType(loc, Material.CHEST);
                fillInventory(loc.getBlock());
                break;

            case 1:
                generateStructure(random, OldStructures.chests3, loc.subtract(0, 1, 0));
                break;

            case 2:
                generateStructure(random, OldStructures.chests2, loc.subtract(0, 1, 0));
                break;

            case 3:
                generateStructure(random, OldStructures.chests1, loc);
                break;

            case 4:
                setType(loc, VMaterial.SKELETON_SKULL_BLOCK.get());
                break;

            case 5:
                setType(loc.clone().add(1, 0, 0), VMaterial.COBBLESTONE_SLAB.get());
                setType(loc.clone().subtract(1, 0, 0), VMaterial.COBBLESTONE_SLAB.get());
                setType(loc.clone().add(0, 0, 1), VMaterial.COBBLESTONE_SLAB.get());
                setType(loc.clone().subtract(0, 0, 1), VMaterial.COBBLESTONE_SLAB.get());
                setType(loc.clone().subtract(0, 1, 0), Material.NETHERRACK);
                setType(loc, Material.FIRE);
                break;

            case 6:
                Location tempL1 = loc.clone().add(1, -1, 0);
                Location tempL2 = loc.clone().add(-1, -1, 0);
                Location tempL3 = loc.clone().add(0, -1, 1);
                Location tempL4 = loc.clone().add(0, -1, -1);
                loc.getBlock().setType(VMaterial.CRAFTING_TABLE.get());
                if(tempL1.getBlock().getType().isSolid() && random.nextInt(3) == 1) {
                    setType(tempL1.add(0, 1, 0), Material.REDSTONE_WIRE);
                }
                if(tempL2.getBlock().getType().isSolid() && random.nextInt(3) == 1) {
                    setType(tempL1.add(0, 1, 0), Material.REDSTONE_WIRE);
                }
                if(tempL3.getBlock().getType().isSolid() && random.nextInt(3) == 1) {
                    setType(tempL1.add(0, 1, 0), Material.REDSTONE_WIRE);
                }
                if(tempL4.getBlock().getType().isSolid() && random.nextInt(3) == 1) {
                    setType(tempL1.add(0, 1, 0), Material.REDSTONE_WIRE);
                }
                break;

            case 7:
                generateStructure(random, OldStructures.sfishs1, loc);
                break;

            case 8:
                generateStructure(random, OldStructures.sfishs2, loc);
                break;

            case 9:
                generateStructure(random, OldStructures.sfishs3, loc);
                break;
        }
    }

    private void generateStructure(Random random, int[][][] rock, Location loc) {
        int xMod = random.nextBoolean() ? -1 : 1;
        int zMod = random.nextBoolean() ? -1 : 1;
        for(int y = 0; y < rock[0].length; y++) for(int x = -1; x < rock.length-1; x++) for(int z = -1; z < rock[0][0].length-1; z++) {
            decideBlock(random, rock[x+1][y][z+1], loc.clone().add(x * xMod, y, z * zMod));
        }
    }

    private void decideBlock(Random random, int type, Location loc) {
        //1 == wood decide 2 == chest 3 == torch 4 == random utility 5 == door 6 = wood stay 7 == Random Ore 8 == Snow Block 9 == Spawner 10 = silverfish stone
        switch (type) {
            case 1:
                if(random.nextBoolean()) setType(loc, VMaterial.OAK_PLANKS.get());
                break;

            case 2:
                setType(loc, Material.CHEST);
                fillInventory(loc.getBlock());
                break;

            case 3:
                setType(loc, Material.TORCH);
                break;

            case 4:
                switch (random.nextInt(15)) {
                    case 0:
                        setType(loc, Material.FURNACE);
                        break;

                    case 1:
                        setType(loc, Material.CHEST);
                        fillInventory(loc.getBlock());
                        break;

                    case 2:
                        setType(loc, VMaterial.CRAFTING_TABLE.get());
                        break;

                    case 3:
                        setType(loc, Material.CAULDRON);
                        break;

                    case 4:
                        setType(loc, Material.ANVIL);
                        break;

                    default:
                        break;
                }
                break;

            case 5:
                setType(loc, VMaterial.OAK_PLANKS.get());
                break;

            case 6:
                setType(loc, VMaterial.SPRUCE_LOG.get());
                break;

            case 7:
                switch (random.nextInt(3)) {
                    default:
                        setType(loc, Material.STONE);
                        break;

                    case 1:
                        setType(loc, Material.COAL_ORE);
                        break;

                    case 2:
                        setType(loc, Material.IRON_ORE);
                        break;
                }
                break;

            case 8:
                setType(loc, Material.SNOW_BLOCK);
                break;

            case 9:
                setType(loc, VMaterial.SPAWNER.get());
                BlockState blockState = loc.getBlock().getState();
                CreatureSpawner spawner = ((CreatureSpawner) blockState);
                spawner.setSpawnedType(EntityType.SILVERFISH);
                blockState.update();
                break;

            case 10:
                setType(loc, VMaterial.INFESTED_STONE.get());
                break;

            default:
                break;
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
