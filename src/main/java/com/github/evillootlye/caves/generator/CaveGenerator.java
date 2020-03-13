package com.github.evillootlye.caves.generator;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.util.Materials;
import com.github.evillootlye.caves.util.Utils;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

//import net.minecraft.server.v1_12_R1.BlockPosition;
//import net.minecraft.server.v1_12_R1.IBlockData;

@Configurable.Path("generator")
public class CaveGenerator extends BlockPopulator implements Configurable {

    private final List<Material> items;
    private double chance;
    private boolean traps;
    private boolean buildings;
    private boolean boulders;
    private boolean pillars;
    private boolean skulls;

    public CaveGenerator() {
        items = new ArrayList<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("chance", 50)/100;
        traps = cfg.getBoolean("structures.traps", true);
        pillars = cfg.getBoolean("structures.pillars", true);
        boulders = cfg.getBoolean("structures.boulders", true);
        buildings = cfg.getBoolean("structures.buildings", true);
        skulls = cfg.getBoolean("structures.skulls", true);

        Set<String> worlds = new HashSet<>();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);

        for(World world : Bukkit.getWorlds()) {
            List<BlockPopulator> populators = world.getPopulators();
            populators.remove(this);
            if(chance > 0 && worlds.contains(world.getName())) populators.add(this);
        }
        items.clear();

        List<String> itemsCfg = cfg.getStringList("chest-items");
        for(String materialStr : itemsCfg) {
            Material material = Material.getMaterial(materialStr.toUpperCase());
            if(material != null) items.add(material);
        }
    }

    @Override
    public void populate(World wor, Random rand, Chunk chnk) {
        if(chance > 0 && chance > rand.nextDouble()) {
            //-1 + | 1 == random pillar or shape / boulder 2 == random skeleton skull 3 == random room with stuff or random chest 4 == monsters spawner surrounded 5 == random mineshaft / tunnel 6 == spiders nest small 7 == traps
            //int typeC = rand.nextInt(8);
            //sendCaveMessage(typeC);
            int cX = chnk.getX() * 16;
            int cZ = chnk.getZ() * 16;
            int cXOff = cX + 7;
            int cZOff = cZ + 7;
            if(rand.nextBoolean()) return;
            switch (rand.nextInt(4)) {
                case 0: if(pillars)     randomShape(rand, cXOff, cZOff, wor); break;
                case 1: if(boulders)    randomBoulder(rand, cXOff, cZOff, wor); break;
                case 2: if(traps)       randomTrap(rand, cXOff, cZOff, wor); break;
                case 3: if(buildings)   randomStructure(rand, cXOff, cZOff, wor); break;
            }
        }
    }

    private int getClosestAir(int cXOff, int cZOff, World w) {
        Location loc = new Location(w, cXOff, 1, cZOff);
        while(loc.getY() < 55) {
            loc.add(0, 1, 0);
            if(Materials.isAir(loc.getBlock().getType())) {
                Location loc2 = new Location(w, loc.getX(), loc.getY() - 1, loc.getZ());
                if(Materials.isCave(loc2.getBlock().getType())) {
                    Location loc3 = new Location(w, loc.getX(), loc.getY() + 1, loc.getZ());
                    if(Materials.isAir(loc3.getBlock().getType())) {
                        break;
                    }
                }
            }
        }
        return loc.getBlockY();
    }

    private Material getRandStone(Random random) {
        return random.nextBoolean() ? Material.STONE : Material.COBBLESTONE;
    }

    private void randomShape(Random random, int cXOff, int cZOff, World w) {
        int yVal = getClosestAir(cXOff, cZOff, w);
        if(yVal != 55) {
            Location loc = new Location(w, cXOff, yVal, cZOff);
            int type = random.nextInt(8);
            if(type==0) {
                loc.getBlock().setType(Material.POLISHED_ANDESITE, false);
                loc.add(0, 1, 0).getBlock().setType(getRandStone(random), false);
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICK_SLAB, false);
            }
            else if(type==1) {
                loc.getBlock().setType(Material.POLISHED_ANDESITE, false);
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICK_SLAB, false);
            }
            else if(type==2) {
                loc.getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                Location loc2 = new Location(loc.getWorld(), loc.getX()+random.nextInt(2), loc.getY(), loc.getZ()+1);
                loc2.getBlock().setType(Material.STONE_BRICK_SLAB, false);
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICK_SLAB, false);
            }
            else if(type==3) {
                loc.getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                Location loc2 = new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ()+random.nextInt(2));
                loc2.getBlock().setType(Material.STONE_BRICK_SLAB, false);
            }
            else if(type==4) {
                loc.getBlock().setType(getRandStone(random), false);
                Location loc2 = new Location(loc.getWorld(), loc.getX()+random.nextInt(2), loc.getY(), loc.getZ()+1);
                loc2.getBlock().setType(Material.STONE_BRICK_SLAB, false);
                loc.add(0,1,0).getBlock().setType(Material.POLISHED_ANDESITE, false);
                Location loc22 = new Location(loc.getWorld(), loc.getX()-1, loc.getY(), loc.getZ()+random.nextInt(2));
                loc22.getBlock().setType(Material.STONE_BRICK_SLAB, false);
            }
            else if(type==5) {
                loc.getBlock().setType(Material.STONE_BRICKS, false);
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()+1);
                loc2.getBlock().setType(Material.STONE_BRICK_SLAB, false);
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                Location loc22 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()-1);
                loc22.getBlock().setType(Material.STONE_BRICK_SLAB, false);
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICK_SLAB, false);
            }
            else if(type==6) {
                loc.getBlock().setType(Material.STONE_BRICKS, false);
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                Location loc2 = new Location(loc.getWorld(), loc.getX()+random.nextInt(2), loc.getY(), loc.getZ()+1);
                loc2.getBlock().setType(Material.STONE_BRICK_SLAB, false);
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                Location loc22 = new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ());
                loc22.getBlock().setType(Material.STONE_BRICK_SLAB, false);
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                Location loc222 = new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ());
                loc222.getBlock().setType(Material.STONE_BRICK_SLAB, false);
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
                if(random.nextBoolean()) {
                    loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
                }
                Location loc2222 = new Location(loc.getWorld(), loc.getX()-1, loc.getY(), loc.getZ());
                loc2222.getBlock().setType(Material.STONE_BRICK_SLAB, false);
                loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICK_SLAB, false);
                Location loc22222 = new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ());
                loc22222.getBlock().setType(Material.STONE_BRICK_SLAB, false);
            }
            else if(type==7) {
                loc.getBlock().setType(Material.COBBLESTONE, false);
                loc.add(0, 1, 0).getBlock().setType(Material.COBBLESTONE_WALL, false);
            }
        }
    }

    private void randomBoulder(Random random, int cXOff, int cZOff, World w) {
        int yVal = getClosestAir(cXOff, cZOff, w);
        if(yVal != 55) {
            Location loc = new Location(w, cXOff, yVal, cZOff);
            int type = random.nextInt(8);
            if(type==0) {
                generateBoulder(random, Structures.rock1, loc);
            }
            else if(type==1) {
                generateBoulder(random, Structures.rock2, loc);
            }
            else if(type==2) {
                generateBoulder(random, Structures.rock3, loc);
            }
            else if(type==3) {
                generateBoulder(random, Structures.rock4, loc);
            }
            else if(type==4) {
                generateBoulder(random, Structures.rock5, loc);
            }
            else if(type==5) {
                generateBoulder(random, Structures.rock6, loc);
            }
            else if(type==6) {
                generateBoulder(random, Structures.rock7, loc);
            }
            else if(type==7) {
                generateBoulder(random, Structures.rock8, loc);
            }
        }
    }

    private void randomTrap(Random random, int cXOff, int cZOff, World w) {
        int yVal = getClosestAir(cXOff, cZOff, w);
        if(yVal != 55) {
            Location loc = new Location(w, cXOff, yVal, cZOff);
            int type = random.nextInt(9);
            if(type==0) {
                //for(int i = yVal ; i > 4 ; i--) {
                while(loc.getY()>4) {
                    loc.subtract(0, 1, 0).getBlock().setType(Material.AIR, false);
                }
            }
            else if(type==1) {
                while(loc.getY()>4) {
                    loc.subtract(0, 1, 0).getBlock().setType(Material.AIR, false);
                }
                loc.add(0, 1, 0).getBlock().setType(Material.LAVA, false);
                loc.add(0, 1, 0).getBlock().setType(Material.LAVA, false);
                loc.add(0, 1, 0).getBlock().setType(Material.LAVA, false);
            }
            else if(type==2) {
                loc.getBlock().setType(Material.STONE_PRESSURE_PLATE, false);
                loc.subtract(0, 1, 0).getBlock().setType(Material.GRAVEL, false);
                loc.subtract(0, 1, 0).getBlock().setType(Material.TNT, false);
                while(loc.getY()>4) {
                    loc.subtract(0, 1, 0).getBlock().setType(Material.AIR, false);
                }
                loc.add(0, 1, 0).getBlock().setType(Material.LAVA, false);
                loc.add(0, 1, 0).getBlock().setType(Material.LAVA, false);
                loc.add(0, 1, 0).getBlock().setType(Material.LAVA, false);
            }
            else if(type==3) {
                loc.getBlock().setType(Material.STONE_PRESSURE_PLATE, false);
                loc.subtract(0, 1, 0).getBlock().setType(Material.GRAVEL, false);
                loc.subtract(0, 1, 0).getBlock().setType(Material.TNT, false);
                while(loc.getY()>4) {
                    loc.subtract(0, 1, 0).getBlock().setType(Material.AIR, false);
                }
                loc.add(0, 1, 0).getBlock().setType(Material.COBWEB, false);
                loc.add(0, 1, 0).getBlock().setType(Material.COBWEB, false);
                loc.add(0, 1, 0).getBlock().setType(Material.COBWEB, false);
            }
            else if(type==4) {
                loc.getBlock().setType(Material.STONE_PRESSURE_PLATE, false);
                loc.subtract(0, 1, 0).getBlock().setType(Material.GRAVEL, false);
                loc.subtract(0, 1, 0).getBlock().setType(Material.TNT, false);
                while(loc.getY()>4) {
                    loc.subtract(0, 1, 0).getBlock().setType(Material.AIR, false);
                }
            }
            else if(type==5) {
                loc.getBlock().setType(Material.STONE_PRESSURE_PLATE, false);
                loc.subtract(0, 1, 0).getBlock().setType(Material.GRAVEL, false);
                loc.subtract(0, 1, 0).getBlock().setType(Material.TNT, false);
                if(random.nextBoolean()) {
                    loc.add(0, 0, 1).getBlock().setType(Material.TNT, false);
                    if(random.nextBoolean()) {
                        loc.add(1, 0, 0).getBlock().setType(Material.TNT, false);
                    }
                }
            }
            else if(type==6) {
                loc.getBlock().setType(Material.STONE_PRESSURE_PLATE, false);
                loc.subtract(0, 2, 0).getBlock().setType(Material.TNT, false);
                if(random.nextBoolean()) {
                    loc.add(0, 0, 1).getBlock().setType(Material.TNT, false);
                    if(random.nextBoolean()) {
                        loc.add(1, 0, 0).getBlock().setType(Material.TNT, false);
                    }
                }
            }
            else if(type==7) {
                loc.getBlock().setType(Material.TRAPPED_CHEST, false);
                fillChest(random, loc.getBlock());
                loc.subtract(0, 2, 0).getBlock().setType(Material.TNT, false);
                if(random.nextBoolean()) {
                    loc.add(0, 0, 1).getBlock().setType(Material.TNT, false);
                    if(random.nextBoolean()) {
                        loc.add(1, 0, 0).getBlock().setType(Material.TNT, false);
                    }
                }
            }
            else if(type==8) {
                loc.getBlock().setType(Material.STONE_PRESSURE_PLATE, false);
                loc.subtract(0, 1, 0).getBlock().setType(Material.DISPENSER, false);
                Dispenser dis = (Dispenser) loc.getBlock().getState();
                Inventory inv = dis.getInventory();
                inv.addItem(new ItemStack(Material.ARROW, random.nextInt(3)+1));
            }
        }
    }

    private void randomStructure(Random random, int cXOff, int cZOff, World w) {
        int yVal = getClosestAir(cXOff, cZOff, w);
        if(yVal != 55) {
            Location loc = new Location(w, cXOff, yVal, cZOff);
            int type = random.nextInt(10);
            if(type==0) {
                loc.getBlock().setType(Material.CHEST, false);
                fillChest(random, loc.getBlock());
            }
            else if(type==1) {
                loc.subtract(0, 1, 0);
                generateStructure(random, Structures.chests3, loc);
            }
            else if(type==2) {
                loc.subtract(0, 1, 0);
                generateStructure(random, Structures.chests2, loc);
            }
            else if(type==3) {
                generateStructure(random, Structures.chests1, loc);
            }
            else if(type==4) {
                if(skulls) {
                    loc.getBlock().setType(Material.SKELETON_SKULL, false);
                }
            }
            else if(type==5) {
                Location tempL1 = new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ());
                Location tempL2 = new Location(loc.getWorld(), loc.getX()-1, loc.getY(), loc.getZ());
                Location tempL3 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()+1);
                Location tempL4 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()-1);
                tempL1.getBlock().setType(Material.COBBLESTONE_SLAB, false);
                tempL2.getBlock().setType(Material.COBBLESTONE_SLAB, false);
                tempL3.getBlock().setType(Material.COBBLESTONE_SLAB, false);
                tempL4.getBlock().setType(Material.COBBLESTONE_SLAB, false);
                Location tempL5 = new Location(loc.getWorld(), loc.getX(), loc.getY()-1, loc.getZ());
                tempL5.getBlock().setType(Material.NETHERRACK, false);
                loc.getBlock().setType(Material.FIRE, false);
            }
            else if(type==6) {
                Location tempL1 = new Location(loc.getWorld(), loc.getX()+1, loc.getY()-1, loc.getZ());
                Location tempL2 = new Location(loc.getWorld(), loc.getX()-1, loc.getY()-1, loc.getZ());
                Location tempL3 = new Location(loc.getWorld(), loc.getX(), loc.getY()-1, loc.getZ()+1);
                Location tempL4 = new Location(loc.getWorld(), loc.getX(), loc.getY()-1, loc.getZ()-1);
                loc.getBlock().setType(Material.CRAFTING_TABLE, false);
                if(!Materials.isAir(tempL1.getBlock().getType())&&random.nextInt(3)==1) {
                    tempL1.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
                }
                if(!Materials.isAir(tempL2.getBlock().getType())&&random.nextInt(3)==1) {
                    tempL2.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
                }
                if(!Materials.isAir(tempL3.getBlock().getType())&&random.nextInt(3)==1) {
                    tempL3.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
                }
                if(!Materials.isAir(tempL4.getBlock().getType())&&random.nextInt(3)==1) {
                    tempL4.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
                }
            }
            else if(type==7) {
                generateStructure(random, Structures.sfishs1, loc);
            }
            else if(type==8) {
                generateStructure(random, Structures.sfishs2, loc);
            }
            else if(type==9) {
                generateStructure(random, Structures.sfishs3, loc);
            }
        }
    }

    private void decideBlock(Random random, int type, Block b) {
        //1 == wood decide 2 == chest 3 == torch 4 == random utility 5 == door 6 = wood stay 7 == Random Ore 8 == Snow Block 9 == Spawner 10 = silverfish stone
        if(type==1) {
            if(random.nextInt(3)!=0) {
                b.setType(Material.OAK_PLANKS, false);
            }
        }
        else if(type == 2) {
            b.setType(Material.CHEST, false);
            fillChest(random, b);
        }
        else if(type == 3) {
            b.setType(Material.TORCH, false);
        }
        else if(type == 4) {
            if(random.nextInt(3)==1) {
                int choice = random.nextInt(5);
                if(choice == 0) {
                    b.setType(Material.FURNACE, false);
                }
                else if(choice == 1) {
                    b.setType(Material.CHEST, false);
                    fillChest(random, b);
                }
                else if(choice == 2) {
                    b.setType(Material.CRAFTING_TABLE, false);
                }
                else if(choice == 3) {
                    b.setType(Material.CAULDRON, false);
                }
                else if(choice == 4) {
                    b.setType(Material.ANVIL, false);
                }
            }
        }
        else if(type == 5) {
            b.setType(Material.OAK_PLANKS, false);
        }
        else if(type == 6) {
            b.setType(Material.OAK_PLANKS, false);
        }
        else if(type == 7) {
            int typer = random.nextInt(3);
            if(typer == 0) {
                b.setType(Material.STONE, false);
            }
            else if(typer == 1) {
                b.setType(Material.COAL_ORE, false);
            }
            else if(typer == 2) {
                b.setType(Material.IRON_ORE, false);
            }
        }
        else if(type == 8) {
            b.setType(Material.SNOW_BLOCK, false);
        }
        else if(type == 9) {
            b.setType(Material.SPAWNER, false);
            BlockState blockState = b.getState();
            CreatureSpawner spawner = ((CreatureSpawner) blockState);
            spawner.setSpawnedType(EntityType.SILVERFISH);
            blockState.update();
        }
        else if(type == 10) {
            b.setType(Material.INFESTED_STONE, false);
        }
    }

    private void generateStructure(Random random, int[][][] rock, Location loc) {
        int randDirection = random.nextInt(4);
        if(randDirection==0) {
            for(int y = 0; y < rock[0].length; y++) {
                for(int x = -1; x < rock.length-1; x++) {
                    for(int z = -1; z < rock[0][0].length-1; z++) {
                        Location loc2 = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()+z);
                        decideBlock(random, rock[x+1][y][z+1], loc2.getBlock());
                    }
                }
            }
        }
        else if(randDirection==1) {
            for(int y = 0; y < rock[0].length; y++) {
                for(int x = -1; x < rock.length-1; x++) {
                    for(int z = -1; z < rock[0][0].length-1; z++) {
                        Location loc2 = new Location(loc.getWorld(), loc.getX()-x, loc.getY()+y, loc.getZ()+z);
                        decideBlock(random, rock[x+1][y][z+1], loc2.getBlock());
                    }
                }
            }
        }
        else if(randDirection==2) {
            for(int y = 0; y < rock[0].length; y++) {
                for(int x = -1; x < rock.length-1; x++) {
                    for(int z = -1; z < rock[0][0].length-1; z++) {
                        Location loc2 = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()-z);
                        decideBlock(random, rock[x+1][y][z+1], loc2.getBlock());
                    }
                }
            }
        }
        else if(randDirection==3) {
            for(int y = 0; y < rock[0].length; y++) {
                for(int x = -1; x < rock.length-1; x++) {
                    for(int z = -1; z < rock[0][0].length-1; z++) {
                        Location loc2 = new Location(loc.getWorld(), loc.getX()-x, loc.getY()+y, loc.getZ()-z);
                        decideBlock(random, rock[x+1][y][z+1], loc2.getBlock());
                    }
                }
            }
        }
    }

    private void generateBoulder(Random random, int[][][] rock, Location loc) {
        int randDirection = random.nextInt(4);
        if(randDirection==0) {
            for(int y = 0; y < rock[0].length; y++) {
                for(int x = -1; x < rock.length-1; x++) {
                    for(int z = -1; z < rock[0][0].length-1; z++) {
                        if(rock[x+1][y][z+1]==1) {
                            Location loc2 = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()+z);
                            loc2.getBlock().setType(getRandStone(random), false);
                        }
                    }
                }
            }
        }
        else if(randDirection==1) {
            for(int y = 0; y < rock[0].length; y++) {
                for(int x = -1; x < rock.length-1; x++) {
                    for(int z = -1; z < rock[0][0].length-1; z++) {
                        if(rock[x+1][y][z+1]==1) {
                            Location loc2 = new Location(loc.getWorld(), loc.getX()-x, loc.getY()+y, loc.getZ()+z);
                            loc2.getBlock().setType(getRandStone(random), false);
                        }
                    }
                }
            }
        }
        else if(randDirection==2) {
            for(int y = 0; y < rock[0].length; y++) {
                for(int x = -1; x < rock.length-1; x++) {
                    for(int z = -1; z < rock[0][0].length-1; z++) {
                        if(rock[x+1][y][z+1]==1) {
                            Location loc2 = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()-z);
                            loc2.getBlock().setType(getRandStone(random), false);
                        }
                    }
                }
            }
        }
        else if(randDirection==3) {
            for(int y = 0; y < rock[0].length; y++) {
                for(int x = -1; x < rock.length-1; x++) {
                    for(int z = -1; z < rock[0][0].length-1; z++) {
                        if(rock[x+1][y][z+1]==1) {
                            Location loc2 = new Location(loc.getWorld(), loc.getX()-x, loc.getY()+y, loc.getZ()-z);
                            loc2.getBlock().setType(getRandStone(random), false);
                        }
                    }
                }
            }
        }
    }

    private void fillChest(Random random, Block block) {
        int itemsCount = random.nextInt(10) + 2;
        Set<ItemStack> items = new HashSet<>();
        for(int i = 0; i < itemsCount; i++) {
            switch(random.nextInt(24)) {
                case 0:
                    items.add(new ItemStack(Material.OAK_PLANKS, random.nextInt(7) + 1)); break;
                case 1:
                    items.add(new ItemStack(Material.TORCH, random.nextInt(7) + 1)); break;
                case 2:
                    items.add(new ItemStack(Material.COBWEB, random.nextInt(4) + 1)); break;
                case 3:
                    items.add(new ItemStack(Material.BONE, random.nextInt(7) + 1)); break;
                case 4:
                    items.add(new ItemStack(Material.STICK, random.nextInt(7) + 1)); break;
                case 5:
                    items.add(new ItemStack(Material.OAK_LOG, random.nextInt(7) + 1)); break;
                case 6:
                    items.add(new ItemStack(Material.WATER_BUCKET)); break;
                case 7:
                    items.add(new ItemStack(Material.WOODEN_PICKAXE)); break;
                case 8:
                    items.add(new ItemStack(Material.STONE_PICKAXE)); break;
                case 9:
                    items.add(new ItemStack(Material.OAK_SAPLING, random.nextInt(2) + 1)); break;
                case 10:
                    items.add(new ItemStack(Material.COAL, random.nextInt(3) + 1)); break;
                case 11:
                    items.add(new ItemStack(Material.BEEF, random.nextInt(3) + 1)); break;
                case 12:
                    items.add(new ItemStack(Material.APPLE, random.nextInt(3) + 1)); break;
                case 13:
                    items.add(new ItemStack(Material.CHICKEN, random.nextInt(3) + 1)); break;
                case 14:
                    items.add(new ItemStack(Material.WHITE_WOOL, random.nextInt(3) + 1)); break;
                case 15:
                    items.add(new ItemStack(Material.BREAD, random.nextInt(3) + 1)); break;
                case 16:
                    items.add(new ItemStack(Material.DIRT, random.nextInt(5) + 1)); break;
                case 17:
                    items.add(new ItemStack(Material.CARROT, random.nextInt(3) + 1)); break;
                case 18:
                    items.add(new ItemStack(Material.COOKIE, random.nextInt(3) + 1)); break;
                case 19:
                    items.add(new ItemStack(Material.WOODEN_AXE)); break;
                case 20:
                    items.add(new ItemStack(Material.STONE_AXE)); break;
                case 21:
                    items.add(new ItemStack(Material.PAPER, random.nextInt(5) + 1)); break;
                case 22:
                    items.add(new ItemStack(Material.SUGAR_CANE, random.nextInt(3) + 1)); break;
                default: {
                    if (this.items.isEmpty())
                        items.add(new ItemStack(this.items.get(Rnd.nextInt(this.items.size())), Rnd.nextInt(3) + 1));
                }
            }
        }
        Chest chest = (Chest) block.getState();
        Inventory inv = chest.getInventory();
        for (ItemStack item : items) {
            int slot = Rnd.nextInt(inv.getSize());
            while (inv.getItem(slot) != null) slot = Rnd.nextInt(inv.getSize());
            inv.setItem(slot, item);
        }
    }
}
