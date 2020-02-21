package com.github.evillootlye.caves.generator;

import com.github.evillootlye.caves.DangerousCaves;
import com.github.evillootlye.caves.DangerousCavesOld;
import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.utils.Rnd;
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
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

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
    private boolean easter;

    public CaveGenerator() {
        items = new ArrayList<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("try-chance");
        traps = cfg.getBoolean("traps");
        pillars = cfg.getBoolean("pillars");
        boulders = cfg.getBoolean("boulders");
        buildings = cfg.getBoolean("buildings");
        skulls = cfg.getBoolean("skulls");
        easter = cfg.getBoolean("easter");
        Set<String> worlds = new HashSet<>();

        List<String> worldsCfg = cfg.getStringList("worlds");
        if(worldsCfg.isEmpty())
            worlds.add(Bukkit.getWorlds().get(0).getName());
        else
            worlds.addAll(worldsCfg);
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
        if(chance > rand.nextDouble()) {
            //-1 + | 1 == random pillar or shape / boulder 2 == random skeleton skull 3 == random room with stuff or random chest 4 == monsters spawner surrounded 5 == random mineshaft / tunnel 6 == spiders nest small 7 == traps
            //int typeC = rand.nextInt(8);
            //sendCaveMessage(typeC);
            int cX = chnk.getX() * 16;
            int cZ = chnk.getZ() * 16;
            int cXOff = cX + 7;
            int cZOff = cZ + 7;
            if(easter && DangerousCavesOld.INSTANCE.roomX == -1 && rand.nextDouble() < 0.1) {
                createEgg(rand, cXOff, cZOff, wor);
            } else {
                if(rand.nextBoolean()) return;
                switch (rand.nextInt(4)) {
                    case 0: if(pillars)     randomShape(rand, cXOff, cZOff, wor); break;
                    case 1: if(boulders)    randomBoulder(rand, cXOff, cZOff, wor); break;
                    case 2: if(traps)       randomTrap(rand, cXOff, cZOff, wor); break;
                    case 3: if(buildings)   randomStructure(rand, cXOff, cZOff, wor); break;
                }
            }
        }
    }

    private int getClosestAir(int cXOff, int cZOff, World w) {
        try {
            Location loc = new Location(w, cXOff, 1, cZOff);
            while(loc.getY()<55) {
                loc.add(0, 1, 0);
                if(isAir(loc.getBlock().getType())) {
                    Location loc2 = new Location(w, loc.getX(), loc.getY() - 1, loc.getZ());
                    if(isStony(loc2.getBlock().getType())) {
                        Location loc3 = new Location(w, loc.getX(), loc.getY() + 1, loc.getZ());
                        if(isAir(loc3.getBlock().getType())) {
                            break;
                        }
                    }
                }
            }
            return (int) loc.getY();
        }
        catch(Exception error) {
            return 1;
        }
    }

    private Material getRandStone(Random random, int define) {
        if(define == 1) {
            if(random.nextBoolean()) {
                return Material.STONE;
            }
            else {
                return Material.COBBLESTONE;
            }
        }
        else {
            return Material.AIR;
        }
    }

    private void randomShape(Random random, int cXOff, int cZOff, World w) {
        try {
            int yVal = getClosestAir(cXOff, cZOff, w);
            if(yVal == 55) {
            }
            else {
                Location loc = new Location(w, cXOff, yVal, cZOff);
                int type = random.nextInt(8);
                if(type==0) {
                    loc.getBlock().setType(Material.POLISHED_ANDESITE, false);
                    loc.add(0, 1, 0).getBlock().setType(getRandStone(random, 1), false);
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
                    loc.getBlock().setType(getRandStone(random, 1), false);
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
        catch(Exception ignored) {

        }
    }

    private void randomBoulder(Random random, int cXOff, int cZOff, World w) {
        try {
            int yVal = getClosestAir(cXOff, cZOff, w);
            if(yVal == 55) {
            }
            else {
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
        catch(Exception ignored) {
        }
    }

    private void randomTrap(Random random, int cXOff, int cZOff, World w) {
        try {
            int yVal = getClosestAir(cXOff, cZOff, w);
            if(yVal == 55) {
            }
            else {
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
        catch(Exception ignored) {
        }
    }

    private void randomStructure(Random random, int cXOff, int cZOff, World w) {
        try {
            int yVal = getClosestAir(cXOff, cZOff, w);
            if(yVal == 55) {
            }
            else {
                Location loc = new Location(w, cXOff, yVal, cZOff);
                int type = random.nextInt(11);
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
                }
                else if(type==6) {
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
                else if(type==7) {
                    Location tempL1 = new Location(loc.getWorld(), loc.getX()+1, loc.getY()-1, loc.getZ());
                    Location tempL2 = new Location(loc.getWorld(), loc.getX()-1, loc.getY()-1, loc.getZ());
                    Location tempL3 = new Location(loc.getWorld(), loc.getX(), loc.getY()-1, loc.getZ()+1);
                    Location tempL4 = new Location(loc.getWorld(), loc.getX(), loc.getY()-1, loc.getZ()-1);
                    loc.getBlock().setType(Material.CRAFTING_TABLE, false);
                    if(tempL1.getBlock().getType()!=Material.AIR&&random.nextInt(3)==1) {
                        tempL1.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
                    }
                    if(tempL2.getBlock().getType()!=Material.AIR&&random.nextInt(3)==1) {
                        tempL2.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
                    }
                    if(tempL3.getBlock().getType()!=Material.AIR&&random.nextInt(3)==1) {
                        tempL3.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
                    }
                    if(tempL4.getBlock().getType()!=Material.AIR&&random.nextInt(3)==1) {
                        tempL4.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
                    }
                }
                else if(type==8) {
                    generateStructure(random, Structures.sfishs1, loc);
                }
                else if(type==9) {
                    generateStructure(random, Structures.sfishs2, loc);
                }
                else if(type==10) {
                    generateStructure(random, Structures.sfishs3, loc);
                }
            }
        }
        catch(Exception ignored) {
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
        try {
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
        catch(Exception ignored) {
        }
    }

    private void generateBoulder(Random random, int[][][] rock, Location loc) {
        try {
            int randDirection = random.nextInt(4);
            if(randDirection==0) {
                for(int y = 0; y < rock[0].length; y++) {
                    for(int x = -1; x < rock.length-1; x++) {
                        for(int z = -1; z < rock[0][0].length-1; z++) {
                            if(rock[x+1][y][z+1]==1) {
                                Location loc2 = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()+z);
                                loc2.getBlock().setType(getRandStone(random,1), false);
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
                                loc2.getBlock().setType(getRandStone(random,1), false);
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
                                loc2.getBlock().setType(getRandStone(random,1), false);
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
                                loc2.getBlock().setType(getRandStone(random,1), false);
                            }
                        }
                    }
                }
            }
        }
        catch(Exception ignored) {
        }
    }

    private boolean isAir(Material m) {
        return m == Material.AIR || m == Material.CAVE_AIR || m == Material.VOID_AIR;
    }

    private boolean isStony(Material m) {
        return m.name().toLowerCase().contains("dirt") || m == Material.STONE || m == Material.MOSSY_COBBLESTONE || m == Material.ANDESITE || m == Material.DIORITE || m == Material.COBBLESTONE || m == Material.GRANITE || m == Material.GRAVEL;
    }

    //

    private void createEgg(Random random, int cXOff, int cZOff, World w) {
        Location l = new Location(w, cXOff, random.nextInt(30)+15, cZOff);
        Location loc = l.clone();
        int radius = 7;
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for(int x = cx - radius; x <= cx + radius; x++){
            for (int z = cz - radius; z <= cz + radius; z++){
                for(int y = (cy - radius); y < (cy + radius); y++){
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));
                    if(dist < radius * radius){
                        Location l2 = new Location(loc.getWorld(), x, y, z);
                        if(dist > (radius-2) * (radius-2)){
                            l2.getBlock().setMetadata("room1", new FixedMetadataValue(DangerousCaves.INSTANCE, 1));
                            if(random.nextInt(2)==0) {
                                l2.getBlock().setType(Material.BONE_BLOCK, false);
                            }
                            else {
                                l2.getBlock().setType(Material.QUARTZ_BLOCK, false);
                            }
                        }
                        else if(dist > (radius-4) * (radius-4)) {
                            l2.getBlock().setMetadata("room1", new FixedMetadataValue(DangerousCaves.INSTANCE, 1));
                            l2.getBlock().setType(Material.OBSIDIAN, false);
                        }
                    }
                }
            }
        }
        generateStructure0(random, Structures.zerozerotwo, l.clone().subtract(2, 3, 3), true, "room2", false, null, false);
        l.clone().add(4, 0, -2).getBlock().setType(Material.AIR, false);
        l.clone().add(4, -1, -2).getBlock().setType(Material.AIR, false);
        l.clone().add(5, 0, -2).getBlock().setType(Material.AIR, false);
        l.clone().add(5, -1, -2).getBlock().setType(Material.AIR, false);
        l.clone().add(5, 0, -1).getBlock().setType(Material.AIR, false);
        l.clone().add(5, -1, -1).getBlock().setType(Material.AIR, false);
        l.clone().add(5, 0, 0).getBlock().setType(Material.AIR, false);
        l.clone().add(5, -1, 0).getBlock().setType(Material.AIR, false);
        l.clone().add(6, 0, 0).getBlock().setType(Material.AIR, false);
        l.clone().add(6, -1, 0).getBlock().setType(Material.AIR, false);
        Location roomEnter = l.clone().add(5, -1, -1);
        DangerousCavesOld.INSTANCE.roomX = (int) roomEnter.getX()+1;
        DangerousCavesOld.INSTANCE.roomY = (int) roomEnter.getY();
        DangerousCavesOld.INSTANCE.roomZ = (int) roomEnter.getZ()+1;
        DangerousCavesOld.INSTANCE.config.set("002roomx", DangerousCavesOld.INSTANCE.roomX);
        DangerousCavesOld.INSTANCE.config.set("002roomy", DangerousCavesOld.INSTANCE.roomY);
        DangerousCavesOld.INSTANCE.config.set("002roomz", DangerousCavesOld.INSTANCE.roomZ);
        DangerousCavesOld.INSTANCE.saveConfig();
    }

    private void decideBlock(Random random, int type, Block b, boolean packet, Player p, boolean overwrite) {
        //0 == air 1 == null 2 == netherwart brick 3 == netherwart block+red concrete powder 4 == barrier 5 ==netherrack & netherwart block
        if(overwrite) {
            if(packet) {
                p.sendBlockChange(b.getLocation(), b.getType().createBlockData());
            }
            else {
                b.setType(b.getType(), false);
            }
        }
        else {
            if(type==-1) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), b.getType().createBlockData());
                }
                else {
                    b.setType(b.getType(), false);
                }
            }
            if(type==0) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.AIR.createBlockData());
                }
                else {
                    b.setType(Material.AIR, false);
                }
            }
            else if(type == 1) {

            }
            else if(type == 2) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.RED_NETHER_BRICKS.createBlockData());
                }
                else {
                    b.setType(Material.RED_NETHER_BRICKS, false);
                }
            }
            else if(type == 3) {
                int choice = random.nextInt(2);
                if(choice == 0) {
                    if(packet) {
                        p.sendBlockChange(b.getLocation(), Material.NETHER_WART_BLOCK.createBlockData());
                    }
                    else {
                        b.setType(Material.NETHER_WART_BLOCK, false);
                    }
                }
                else if(choice == 1) {
                    if(packet) {
                        p.sendBlockChange(b.getLocation(), Material.RED_CONCRETE_POWDER.createBlockData());
                    }
                    else {
                        b.setType(Material.RED_CONCRETE_POWDER, false);
                    }
                }
            }
            else if(type == 4) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.BARRIER.createBlockData());
                }
                else {
                    b.setType(Material.BARRIER, false);
                }
            }
            else if(type == 5) {
                int choice = random.nextInt(2);
                if(choice == 0) {
                    if(packet) {
                        p.sendBlockChange(b.getLocation(), Material.NETHER_WART_BLOCK.createBlockData());
                    }
                    else {
                        b.setType(Material.NETHER_WART_BLOCK, false);
                    }
                }
                else if(choice == 1) {
                    if(packet) {
                        p.sendBlockChange(b.getLocation(), Material.NETHERRACK.createBlockData());
                    }
                    else {
                        b.setType(Material.NETHERRACK, false);
                    }
                }
            }
            else if(type == 6) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.RED_CONCRETE_POWDER.createBlockData());
                }
                else {
                    b.setType(Material.RED_CONCRETE_POWDER, false);
                }
            }
            else if(type == 7) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.NETHER_WART_BLOCK.createBlockData());
                }
                else {
                    b.setType(Material.NETHER_WART_BLOCK, false);
                }
            }
            else if(type == 8) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.RED_STAINED_GLASS_PANE.createBlockData());
                }
                else {
                    b.setType(Material.RED_STAINED_GLASS_PANE, false);
                }
            }
            else if(type == 9) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.QUARTZ_BLOCK.createBlockData());
                }
                else {
                    b.setType(Material.QUARTZ_BLOCK, false);
                }
            }
            else if(type == 10) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.QUARTZ_SLAB.createBlockData());
                }
                else {
                    b.setType(Material.QUARTZ_SLAB, false);
                }
            }
            else if(type == 11) {
                if(!Bukkit.getServer().getBukkitVersion().contains("1.13")) {
                    if(packet) {
                        p.sendBlockChange(b.getLocation(), Material.RED_NETHER_BRICK_WALL.createBlockData());
                    }
                    else {
                        b.setType(Material.RED_NETHER_BRICK_WALL, false);
                    }
                }
            }
            else if(type == 12) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.BONE_BLOCK.createBlockData());
                }
                else {
                    b.setType(Material.BONE_BLOCK, false);
                }
            }
            else if(type == 13) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.RED_SHULKER_BOX.createBlockData());
                }
                else {
                    b.setType(Material.RED_SHULKER_BOX, false);
                }
            }
            else if(type == 14) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.NETHER_BRICK_SLAB.createBlockData());
                }
                else {
                    b.setType(Material.NETHER_BRICK_SLAB, false);
                }
            }
            else if(type == 15) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.RED_WOOL.createBlockData());
                }
                else {
                    b.setType(Material.RED_WOOL, false);
                }
            }
            else if(type == 16) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.NETHER_BRICK_SLAB.createBlockData());
                }
                else {
                    b.setType(Material.NETHER_BRICK_SLAB, false);
                }
            }
            else if(type == 17) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.REDSTONE_TORCH.createBlockData());
                }
                else {
                    b.setType(Material.REDSTONE_TORCH, false);
                }
            }
            else if(type == 18) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.SKELETON_SKULL.createBlockData());
                }
                else {
                    b.setType(Material.SKELETON_SKULL, false);
                }
            }
            else if(type == 19) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.RED_CARPET.createBlockData());
                }
                else {
                    b.setType(Material.RED_CARPET, false);
                }
            }
            else if(type == 20) {
                if(packet) {
                    p.sendBlockChange(b.getLocation(), Material.REDSTONE_WIRE.createBlockData());
                }
                else {
                    b.setType(Material.REDSTONE_WIRE, false);
                }
            }
        }
    }

    private void generateStructure0(Random random, int[][][] structure, Location loc, boolean hasMeta, String meta, boolean packet, Player p, boolean overwrite) {
        try {
            int randDirection = 0;//Rnd.nextInt(4);
            if(randDirection==0) {
                for(int y = 0; y < structure[0].length; y++) {
                    for(int x = -1; x < structure.length-1; x++) {
                        for(int z = -1; z < structure[0][0].length-1; z++) {
                            Location loc2 = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()+z);
                            decideBlock(random, structure[x+1][y][z+1], loc2.getBlock(), packet, p, overwrite);
                            if(hasMeta) {
                                loc2.getBlock().setMetadata(meta, new FixedMetadataValue(DangerousCaves.INSTANCE, 1));
                            }
                        }
                    }
                }
            }
            else if(randDirection==1) {
                for(int y = 0; y < structure[0].length; y++) {
                    for(int x = -1; x < structure.length-1; x++) {
                        for(int z = -1; z < structure[0][0].length-1; z++) {
                            Location loc2 = new Location(loc.getWorld(), loc.getX()-x, loc.getY()+y, loc.getZ()+z);
                            decideBlock(random, structure[x+1][y][z+1], loc2.getBlock(), packet, p, overwrite);
                            if(hasMeta) {
                                loc2.getBlock().setMetadata(meta, new FixedMetadataValue(DangerousCaves.INSTANCE, 1));
                            }
                        }
                    }
                }
            }
            else if(randDirection==2) {
                for(int y = 0; y < structure[0].length; y++) {
                    for(int x = -1; x < structure.length-1; x++) {
                        for(int z = -1; z < structure[0][0].length-1; z++) {
                            Location loc2 = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()-z);
                            decideBlock(random, structure[x+1][y][z+1], loc2.getBlock(), packet, p, overwrite);
                            if(hasMeta) {
                                loc2.getBlock().setMetadata(meta, new FixedMetadataValue(DangerousCaves.INSTANCE, 1));
                            }
                        }
                    }
                }
            }
            else if(randDirection==3) {
                for(int y = 0; y < structure[0].length; y++) {
                    for(int x = -1; x < structure.length-1; x++) {
                        for(int z = -1; z < structure[0][0].length-1; z++) {
                            Location loc2 = new Location(loc.getWorld(), loc.getX()-x, loc.getY()+y, loc.getZ()-z);
                            decideBlock(random, structure[x+1][y][z+1], loc2.getBlock(), packet, p, overwrite);
                            if(hasMeta) {
                                loc2.getBlock().setMetadata(meta, new FixedMetadataValue(DangerousCaves.INSTANCE, 1));
                            }
                        }
                    }
                }
            }
        }
        catch(Exception ignored) {
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
                    if (this.items.size() > 0)
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
