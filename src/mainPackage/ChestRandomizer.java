package mainPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestRandomizer {

	static Random randor = new Random();
	
	public static void fillChest(Block b) {
         Chest chest = (Chest) b.getState();
         Inventory inv = chest.getInventory();
         List<ItemStack> items = getItems();
         for(ItemStack i : items) {
        	 try {
        	 inv.setItem(randor.nextInt(inv.getSize())+1 , i);
        	 }
        	 catch(Exception e) {
        		 
        	 }
         }
         //chest.update();
	}
	
	public static List<ItemStack> getItems(){
		List<ItemStack> items = new ArrayList<ItemStack>();
		int randomAmmount = randor.nextInt(10)+2;
		for(int i = 0; i < randomAmmount; i++) {
			int choice = randor.nextInt(24);
			if(choice == 0) {
				items.add(new ItemStack(Material.OAK_PLANKS, randor.nextInt(7)+1));
			}
			else if(choice == 1) {
				items.add(new ItemStack(Material.TORCH, randor.nextInt(7)+1));
			}
			else if(choice == 2) {
				items.add(new ItemStack(Material.COBWEB, randor.nextInt(4)+1));
			}
			else if(choice == 3) {
				items.add(new ItemStack(Material.BONE, randor.nextInt(7)+1));
			}
			else if(choice == 4) {
				items.add(new ItemStack(Material.STICK, randor.nextInt(7)+1));
			}
			else if(choice == 5) {
				items.add(new ItemStack(Material.OAK_LOG, randor.nextInt(7)+1));
			}
			else if(choice == 6) {
				items.add(new ItemStack(Material.WATER_BUCKET, 1));
			}
			else if(choice == 7) {
				items.add(new ItemStack(Material.WOODEN_PICKAXE, 1));
			}
			else if(choice == 8) {
				items.add(new ItemStack(Material.STONE_PICKAXE, 1));
			}
			else if(choice == 9) {
				items.add(new ItemStack(Material.OAK_SAPLING, randor.nextInt(2)+1));
			}
			else if(choice == 10) {
				items.add(new ItemStack(Material.COAL, randor.nextInt(3)+1));
			}
			else if(choice == 11) {
				items.add(new ItemStack(Material.BEEF, randor.nextInt(3)+1));
			}
			else if(choice == 12) {
				items.add(new ItemStack(Material.APPLE, randor.nextInt(3)+1));
			}
			else if(choice == 13) {
				items.add(new ItemStack(Material.CHICKEN, randor.nextInt(3)+1));
			}
			else if(choice == 14) {
				items.add(new ItemStack(Material.WHITE_WOOL, randor.nextInt(3)+1));
			}
			else if(choice == 15) {
				items.add(new ItemStack(Material.BREAD, randor.nextInt(3)+1));
			}
			else if(choice == 16) {
				items.add(new ItemStack(Material.DIRT, randor.nextInt(5)+1));
			}
			else if(choice == 17) {
				items.add(new ItemStack(Material.CARROT, randor.nextInt(3)+1));
			}
			else if(choice == 18) {
				items.add(new ItemStack(Material.COOKIE, randor.nextInt(3)+1));
			}
			else if(choice == 19) {
				items.add(new ItemStack(Material.WOODEN_AXE, 1));
			}
			else if(choice == 20) {
				items.add(new ItemStack(Material.STONE_AXE, 1));
			}
			else if(choice == 21) {
				items.add(new ItemStack(Material.PAPER, randor.nextInt(5)+1));
			}
			else if(choice == 22) {
				items.add(new ItemStack(Material.SUGAR_CANE, randor.nextInt(3)+1));
			}
			else if(choice == 23) {
				try {
					if(main.itemcustom.size()>0) {
						items.add(new ItemStack(Material.getMaterial(main.itemcustom.get(randor.nextInt(main.itemcustom.size()))), randor.nextInt(3)+1));
					}
				}
				catch(Exception e) {
					
				}
			}
		}
		return items;
	}

}
