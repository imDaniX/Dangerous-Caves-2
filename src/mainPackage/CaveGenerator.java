package mainPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

//import net.minecraft.server.v1_12_R1.BlockPosition;
//import net.minecraft.server.v1_12_R1.IBlockData;

public class CaveGenerator extends BlockPopulator {
	
	Random randor = new Random();
	/*int[][][] rock5 = { { {0, 0, 0}, {0, 0, 0}, {0, 0, 0} },
						{   {0, 0, 0}, {0, 0, 0}, {0, 0, 0} },
						{   {0, 0, 0}, {0, 0, 0}, {0, 0, 0} } };*/
	int[][][] rock1 = { { {0, 1, 0}, {0, 1, 0}, {0, 0, 0} },
						{ {0, 1, 1}, {0, 1, 0}, {0, 1, 0} },
						{ {0, 0, 0}, {0, 0, 0}, {0, 0, 0} } };
	int[][][] rock2 = { { {0, 1, 0}, {0, 1, 0}, {0, 0, 0} },
						{ {1, 1, 1}, {1, 1, 1}, {0, 1, 0} },
						{ {0, 1, 1}, {0, 0, 0}, {0, 0, 0} } };
	int[][][] rock3 = { { {0, 1, 0}, {0, 0, 0}, {0, 0, 0} },
						{ {0, 1, 1}, {0, 1, 0}, {0, 0, 0} },
						{ {0, 0, 0}, {0, 0, 0}, {0, 0, 0} } };
	int[][][] rock4 = { { {0, 1, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0} },
						{ {1, 1, 1}, {0, 1, 1}, {0, 1, 1}, {0, 1, 0} },
						{ {0, 1, 0}, {0, 1, 0}, {0, 0, 0}, {0, 0, 0} } };
	int[][][] rock5 = { { {0, 1, 0}, {0, 0, 0}, {0, 0, 0} },
						{ {1, 1, 1}, {0, 1, 1}, {0, 0, 1} },
						{ {1, 1, 0}, {0, 1, 0}, {0, 0, 0} } };
	int[][][] rock6 = { { {1, 1, 1}, {0, 1, 1}, {0, 1, 1}, {0, 1, 1}, {0, 0, 1} },
						{ {1, 1, 1}, {1, 1, 1}, {1, 1, 0}, {0, 0, 0}, {0, 0, 0} },
						{ {1, 1, 0}, {0, 1, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0} } };
	int[][][] rock7 = { { {1, 1, 0}, {1, 0, 0}, {0, 0, 0} },
						{ {1, 1, 1}, {1, 1, 1}, {1, 1, 0} },
						{ {1, 1, 1}, {0, 1, 0}, {0, 0, 0} } };
	int[][][] rock8 = { { {0, 1, 0}, {0, 0, 0}, {0, 0, 0} },
					{     {1, 1, 1}, {0, 1, 0}, {0, 0, 0} },
					{     {0, 1, 0}, {0, 0, 0}, {0, 0, 0} } };
	int[][][] chests1 = { { {0, 6, 0}, {0, 6, 0}, {0, 0, 0} },
						{   {6, 2, 6}, {6, 0, 0}, {0, 0, 0} },
						{   {0, 5, 0}, {0, 0, 0}, {0, 0, 0} } };
	//1 == wood decide 2 == chest 3 == torch 4 == random utility 5 == door 6 = wood stay 7 == Random Ore 8 == Snow Block 9 == Spawner 10 = silverfish stone
	int[][][] chests2 = { { {1, 1, 1, 1, 1}, {0, 1, 1, 1, 0}, {0, 1, 1, 1, 0}, {0, 1, 1, 1, 0}, {0, 0, 0, 0, 0} },
						{   {1, 1, 1, 1, 1}, {1, 4, 4, 4, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}, {0, 1, 1, 1, 0} },
						{   {1, 1, 1, 1, 1}, {1, 4, 3, 4, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}, {0, 1, 1, 1, 0} },
						{   {1, 1, 1, 1, 1}, {1, 4, 0, 4, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}, {0, 1, 1, 1, 0} },
						{   {1, 1, 1, 1, 1}, {0, 1, 0, 1, 0}, {0, 1, 0, 1, 0}, {0, 1, 1, 1, 0}, {0, 0, 0, 0, 0} } };
	int[][][] chests3 = { { {1, 1, 1}, {0, 0, 0}, {0, 0, 0} },
						{   {1, 1, 1}, {0, 2, 0}, {0, 0, 0} },
						{   {1, 1, 1}, {0, 0, 0}, {0, 0, 0} } };
	int[][][] sfishs1 = { { {0, 7, 0}, {0, 0, 0}, {0, 0, 0} },
						{   {7, 9, 7}, {0, 7, 0}, {0, 0, 0} },
						{   {0, 7, 0}, {0, 0, 0}, {0, 0, 0} } };
	int[][][] sfishs2 = { { {0, 10, 0}, {0, 10, 0}, {0, 0, 0} },
						{   {10, 9, 10}, {0, 8, 0}, {0, 0, 0} },
						{   {0, 10, 8}, {0, 0, 0}, {0, 0, 0} } };
	int[][][] sfishs3 = { { {0, 10, 0}, {0, 10, 0}, {0, 0, 0} },
						{   {10, 9, 10}, {0, 8, 10}, {0, 10, 0} },
						{   {0, 10, 8}, {0, 0, 0}, {0, 0, 0} } };
	
	@Override
	public void populate(World wor, Random rand, Chunk chnk) {
		
		if(rand.nextInt(main.cavechance+1)==0&&main.cavestruct==true) {
		//-1 + | 1 == random pillar or shape / boulder 2 == random skeleton skull 3 == random room with stuff or random chest 4 == monsters spawner surrounded 5 == random mineshaft / tunnel 6 == spiders nest small 7 == traps
		//int typeC = rand.nextInt(8);
			int typeC = rand.nextInt(4);
		//sendCaveMessage(typeC);
			int cX = chnk.getX() * 16;
			int cZ = chnk.getZ() * 16;
			int cXOff = cX + 7;
			int cZOff = cZ + 7;
			if(main.instance.roomX == -1 && randor.nextInt(100)==1) {
				if(main.easter) {
				createEgg(cXOff, cZOff, wor);
				}
			}
			else {
    		if(typeC==0) {
    			if(randor.nextInt(main.plrate+1)==0) {
    			randomShape(cXOff, cZOff, wor);
    			}
    		}
    		else if(typeC==1) {
    			if(randor.nextInt(main.blrate+1)==0) {
    			randomBoulder(cXOff, cZOff, wor);
    			}
    		}
    		else if(typeC==2) {
    			if(randor.nextInt(main.trrate+1)==0) {
    			randomTrap(cXOff, cZOff, wor);
    			}
    		}
    		else if(typeC==3) {
    			if(randor.nextInt(main.strate+1)==0) {
    				randomStructure(cXOff, cZOff, wor);
    			}
    		}
			}
		}
	}
	
	public void sendCaveMessage(int typeC) {
		if(typeC==0) {
    		Bukkit.getServer().getConsoleSender().sendMessage("!Generated Spider Den!");
    		}
    		else if(typeC==1){
    			Bukkit.getServer().getConsoleSender().sendMessage("!Generated Mushroom Cave!");
    		}
    		else if(typeC==2){
    			Bukkit.getServer().getConsoleSender().sendMessage("!Generated Andesite Cave!");
    		}
    		else if(typeC==3){
    			Bukkit.getServer().getConsoleSender().sendMessage("!Generated Granite Cave!");
    		}
    		else if(typeC==4){
    			Bukkit.getServer().getConsoleSender().sendMessage("!Generated Diorite Cave!");
    		}
    		else if(typeC==5){
    			Bukkit.getServer().getConsoleSender().sendMessage("!Generated Lava Cave!");
    		}
    		else if(typeC==6){
    			Bukkit.getServer().getConsoleSender().sendMessage("!Generated Sandy Cave!");
    		}
    		else if(typeC==7){
    			Bukkit.getServer().getConsoleSender().sendMessage("!Generated Snow Cave!");
    		}
    		else if(typeC==8){
    			Bukkit.getServer().getConsoleSender().sendMessage("!Generated Flooded Cave!");
    		}
	}
	
	public int getClosestAir(int cXOff, int cZOff, World w) {
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
	
	public Material getRandStone(int define) {
		if(define == 1) {
			if(randor.nextBoolean()==true) {
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
	
	public void randomShape(int cXOff, int cZOff, World w) {
		try {
		int yVal = getClosestAir(cXOff, cZOff, w);
		if(yVal == 55) {
			return;
		}
		else {
		Location loc = new Location(w, cXOff, yVal, cZOff);
		int type = randor.nextInt(8);
		if(type==0) {
			loc.getBlock().setType(Material.POLISHED_ANDESITE, false);
			loc.add(0, 1, 0).getBlock().setType(getRandStone(1), false);
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICK_SLAB, false);
		}
		else if(type==1) {
			loc.getBlock().setType(Material.POLISHED_ANDESITE, false);
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICK_SLAB, false);
		}
		else if(type==2) {
			loc.getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			Location loc2 = new Location(loc.getWorld(), loc.getX()+randor.nextInt(2), loc.getY(), loc.getZ()+1);
			loc2.getBlock().setType(Material.STONE_BRICK_SLAB, false);
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICK_SLAB, false);
		}
		else if(type==3) {
			loc.getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			Location loc2 = new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ()+randor.nextInt(2));
			loc2.getBlock().setType(Material.STONE_BRICK_SLAB, false);
		}
		else if(type==4) {
			loc.getBlock().setType(getRandStone(1), false);
			Location loc2 = new Location(loc.getWorld(), loc.getX()+randor.nextInt(2), loc.getY(), loc.getZ()+1);
			loc2.getBlock().setType(Material.STONE_BRICK_SLAB, false);
			loc.add(0,1,0).getBlock().setType(Material.POLISHED_ANDESITE, false);
			Location loc22 = new Location(loc.getWorld(), loc.getX()-1, loc.getY(), loc.getZ()+randor.nextInt(2));
			loc22.getBlock().setType(Material.STONE_BRICK_SLAB, false);
		}
		else if(type==5) {
			loc.getBlock().setType(Material.STONE_BRICKS, false);
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()+1);
			loc2.getBlock().setType(Material.STONE_BRICK_SLAB, false);
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			Location loc22 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()-1);
			loc22.getBlock().setType(Material.STONE_BRICK_SLAB, false);
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICK_SLAB, false);
		}
		else if(type==6) {
			loc.getBlock().setType(Material.STONE_BRICKS, false);
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			Location loc2 = new Location(loc.getWorld(), loc.getX()+randor.nextInt(2), loc.getY(), loc.getZ()+1);
			loc2.getBlock().setType(Material.STONE_BRICK_SLAB, false);
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			Location loc22 = new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ());
			loc22.getBlock().setType(Material.STONE_BRICK_SLAB, false);
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
				loc.getBlock().setType(Material.CRACKED_STONE_BRICKS, false);
			}
			Location loc222 = new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ());
			loc222.getBlock().setType(Material.STONE_BRICK_SLAB, false);
			loc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICKS, false);
			if(randor.nextBoolean()==true) {
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
	catch(Exception error) {
			
	}
	}
	
	public void randomBoulder(int cXOff, int cZOff, World w) {
		try {
		int yVal = getClosestAir(cXOff, cZOff, w);
		if(yVal == 55) {
			return;
		}
		else {
		Location loc = new Location(w, cXOff, yVal, cZOff);
		int type = randor.nextInt(8);
			if(type==0) {
				generateBoulder(rock1, loc);
			}
			else if(type==1) {
				generateBoulder(rock2, loc);
			}
			else if(type==2) {
				generateBoulder(rock3, loc);
			}
			else if(type==3) {
				generateBoulder(rock4, loc);
			}
			else if(type==4) {
				generateBoulder(rock5, loc);
			}
			else if(type==5) {
				generateBoulder(rock6, loc);
			}
			else if(type==6) {
				generateBoulder(rock7, loc);
			}
			else if(type==7) {
				generateBoulder(rock8, loc);
			}
		}
		}
		catch(Exception error) {
		}
	}
	
	public void randomTrap(int cXOff, int cZOff, World w) {
		try {
		int yVal = getClosestAir(cXOff, cZOff, w);
		if(yVal == 55) {
			return;
		}
		else {
		Location loc = new Location(w, cXOff, yVal, cZOff);
		int type = randor.nextInt(9);
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
				if(randor.nextBoolean()==true) {
					loc.add(0, 0, 1).getBlock().setType(Material.TNT, false);
					if(randor.nextBoolean()==true) {
						loc.add(1, 0, 0).getBlock().setType(Material.TNT, false);
					}
				}
			}
			else if(type==6) {
				loc.getBlock().setType(Material.STONE_PRESSURE_PLATE, false);
				loc.subtract(0, 2, 0).getBlock().setType(Material.TNT, false);
				if(randor.nextBoolean()==true) {
					loc.add(0, 0, 1).getBlock().setType(Material.TNT, false);
					if(randor.nextBoolean()==true) {
						loc.add(1, 0, 0).getBlock().setType(Material.TNT, false);
					}
				}
			}
			else if(type==7) {
				loc.getBlock().setType(Material.TRAPPED_CHEST, false);
				ChestRandomizer.fillChest(loc.getBlock());
				loc.subtract(0, 2, 0).getBlock().setType(Material.TNT, false);
				if(randor.nextBoolean()==true) {
					loc.add(0, 0, 1).getBlock().setType(Material.TNT, false);
					if(randor.nextBoolean()==true) {
						loc.add(1, 0, 0).getBlock().setType(Material.TNT, false);
					}
				}
			}
			else if(type==8) {
				loc.getBlock().setType(Material.STONE_PRESSURE_PLATE, false);
				loc.subtract(0, 1, 0).getBlock().setType(Material.DISPENSER, false);
				Dispenser dis = (Dispenser) loc.getBlock().getState();
		        Inventory inv = dis.getInventory();
		        inv.addItem(new ItemStack(Material.ARROW, randor.nextInt(3)+1));
			}
		}
		}
		catch(Exception error) {
		}
	}
	
	public void randomStructure(int cXOff, int cZOff, World w) {
		try {
		int yVal = getClosestAir(cXOff, cZOff, w);
		if(yVal == 55) {
			return;
		}
		else {
		Location loc = new Location(w, cXOff, yVal, cZOff);
		int type = randor.nextInt(11);
			if(type==0) {
				loc.getBlock().setType(Material.CHEST, false);
				ChestRandomizer.fillChest(loc.getBlock());
			}
			else if(type==1) {
				loc.subtract(0, 1, 0);
				generateStructure(chests3, loc);
			}
			else if(type==2) {
				loc.subtract(0, 1, 0);
				generateStructure(chests2, loc);
			}
			else if(type==3) {
				generateStructure(chests1, loc);
			}
			else if(type==4) {
				if(main.skulls) {
				loc.getBlock().setType(Material.SKELETON_SKULL, false);
				}
			}
			else if(type==5) {
				return;
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
				if(tempL1.getBlock().getType()!=Material.AIR&&randor.nextInt(3)==1) {
					tempL1.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
				}
				if(tempL2.getBlock().getType()!=Material.AIR&&randor.nextInt(3)==1) {
					tempL2.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
				}
				if(tempL3.getBlock().getType()!=Material.AIR&&randor.nextInt(3)==1) {
					tempL3.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
				}
				if(tempL4.getBlock().getType()!=Material.AIR&&randor.nextInt(3)==1) {
					tempL4.add(0, 1, 0).getBlock().setType(Material.REDSTONE_WIRE, false);
				}
			}
			else if(type==8) {
				generateStructure(sfishs1, loc);
			}
			else if(type==9) {
				generateStructure(sfishs2, loc);
			}
			else if(type==10) {
				generateStructure(sfishs3, loc);
			}
		}
		}
		catch(Exception error) {
		}
	}
	
	public void decideBlock(int type, Block b) {
		//1 == wood decide 2 == chest 3 == torch 4 == random utility 5 == door 6 = wood stay 7 == Random Ore 8 == Snow Block 9 == Spawner 10 = silverfish stone
		if(type==1) {
			if(randor.nextInt(3)!=0) {
				b.setType(Material.OAK_PLANKS, false);
			}
		}
		else if(type == 2) {
			b.setType(Material.CHEST, false);
			ChestRandomizer.fillChest(b);
		}
		else if(type == 3) {
			b.setType(Material.TORCH, false);
		}
		else if(type == 4) {
			if(randor.nextInt(3)==1) {
			int choice = randor.nextInt(5);
			if(choice == 0) {
				b.setType(Material.FURNACE, false);
			}
			else if(choice == 1) {
				b.setType(Material.CHEST, false);
				ChestRandomizer.fillChest(b);
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
			int typer = randor.nextInt(3);
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
	
	public void generateStructure(int[][][] rock, Location loc) {
		try {
		int randDirection = randor.nextInt(4);
		if(randDirection==0) {
			for(int y = 0; y < rock[0].length; y++) {
				for(int x = -1; x < rock.length-1; x++) {
					for(int z = -1; z < rock[0][0].length-1; z++) {
						Location loc2 = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()+z);
						decideBlock(rock[x+1][y][z+1], loc2.getBlock());
					}	
				}
			}
		}
		else if(randDirection==1) {
			for(int y = 0; y < rock[0].length; y++) {
				for(int x = -1; x < rock.length-1; x++) {
					for(int z = -1; z < rock[0][0].length-1; z++) {
						Location loc2 = new Location(loc.getWorld(), loc.getX()-x, loc.getY()+y, loc.getZ()+z);
						decideBlock(rock[x+1][y][z+1], loc2.getBlock());
					}	
				}
			}
		}
		else if(randDirection==2) {
			for(int y = 0; y < rock[0].length; y++) {
				for(int x = -1; x < rock.length-1; x++) {
					for(int z = -1; z < rock[0][0].length-1; z++) {
						Location loc2 = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()-z);
						decideBlock(rock[x+1][y][z+1], loc2.getBlock());
					}	
				}
			}
		}
		else if(randDirection==3) {
			for(int y = 0; y < rock[0].length; y++) {
				for(int x = -1; x < rock.length-1; x++) {
					for(int z = -1; z < rock[0][0].length-1; z++) {
						Location loc2 = new Location(loc.getWorld(), loc.getX()-x, loc.getY()+y, loc.getZ()-z);
						decideBlock(rock[x+1][y][z+1], loc2.getBlock());
					}	
				}
			}
		}
		}
		catch(Exception error) {
		}
	}
	
	public void generateBoulder(int[][][] rock, Location loc) {
		try {
		int randDirection = randor.nextInt(4);
		if(randDirection==0) {
			for(int y = 0; y < rock[0].length; y++) {
				for(int x = -1; x < rock.length-1; x++) {
					for(int z = -1; z < rock[0][0].length-1; z++) {
						if(rock[x+1][y][z+1]==1) {
						Location loc2 = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()+z);
						loc2.getBlock().setType(getRandStone(1), false);
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
						loc2.getBlock().setType(getRandStone(1), false);
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
						loc2.getBlock().setType(getRandStone(1), false);
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
						loc2.getBlock().setType(getRandStone(1), false);
						}
					}	
				}
			}
		}
		}
		catch(Exception error) {
		}
	}
	
	public boolean isAir(Material m) {
		if(m == Material.AIR || m == Material.CAVE_AIR || m == Material.VOID_AIR) {
			return true;
		}
		return false;
	}
	
	public boolean isStony(Material m) {
		if(m.name().toLowerCase().contains("dirt") || m == Material.STONE || m == Material.MOSSY_COBBLESTONE || m == Material.ANDESITE || m == Material.DIORITE || m == Material.COBBLESTONE || m == Material.GRANITE || m == Material.GRAVEL) {
			return true;
		}
		return false;
	}
	
	//
	
	public void createEgg(int cXOff, int cZOff, World w) {
		Location l = new Location(w, cXOff, randor.nextInt(30)+15, cZOff);
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
					l2.getBlock().setMetadata("room1", new FixedMetadataValue(main.instance, 1));
					if(randor.nextInt(2)==0) {
						l2.getBlock().setType(Material.BONE_BLOCK, false);
					}
					else {
						l2.getBlock().setType(Material.QUARTZ_BLOCK, false);
					}
				}
				else if(dist > (radius-4) * (radius-4)) {
					l2.getBlock().setMetadata("room1", new FixedMetadataValue(main.instance, 1));
					l2.getBlock().setType(Material.OBSIDIAN, false);
				}
			}
		}
		}
		}
		generateStructure0(structurefiles.zerozerotwo, l.clone().subtract(2, 3, 3), true, "room2", false, null, 0, false);
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
		main.instance.roomX = (int) roomEnter.getX()+1;
		main.instance.roomY = (int) roomEnter.getY();
		main.instance.roomZ = (int) roomEnter.getZ()+1;
		main.instance.config.set("002roomx", main.instance.roomX);
		main.instance.config.set("002roomy", main.instance.roomY);
		main.instance.config.set("002roomz", main.instance.roomZ);
		main.instance.saveConfig();
	}
	
	public void decideBlock(int type, Block b, boolean packet, Player p, boolean overwrite) {
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
			int choice = randor.nextInt(2);
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
			int choice = randor.nextInt(2);
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

	public void generateStructure0(int[][][] structure, Location loc, boolean hasMeta, String meta, boolean packet, Player p, int direction, boolean overwrite) {
		try {
		int randDirection = 0;//randor.nextInt(4);
		if(randDirection==0) {
			for(int y = 0; y < structure[0].length; y++) {
				for(int x = -1; x < structure.length-1; x++) {
					for(int z = -1; z < structure[0][0].length-1; z++) {
						Location loc2 = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()+z);
						decideBlock(structure[x+1][y][z+1], loc2.getBlock(), packet, p, overwrite);
						if(hasMeta) {
							loc2.getBlock().setMetadata(meta, new FixedMetadataValue(main.instance, 1));
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
						decideBlock(structure[x+1][y][z+1], loc2.getBlock(), packet, p, overwrite);
						if(hasMeta) {
							loc2.getBlock().setMetadata(meta, new FixedMetadataValue(main.instance, 1));
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
						decideBlock(structure[x+1][y][z+1], loc2.getBlock(), packet, p, overwrite);
						if(hasMeta) {
							loc2.getBlock().setMetadata(meta, new FixedMetadataValue(main.instance, 1));
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
						decideBlock(structure[x+1][y][z+1], loc2.getBlock(), packet, p, overwrite);
						if(hasMeta) {
							loc2.getBlock().setMetadata(meta, new FixedMetadataValue(main.instance, 1));
						}
					}	
				}
			}
		}
		}
		catch(Exception error) {
		}
	}

	
}
