package mainPackage;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class DressCursed {

	public static Random rand = new Random();
	
	public static void dressDGolem(LivingEntity s) {
		try {
		EntityEquipment ee = (s).getEquipment();
		//chest
		ItemStack lchest = new ItemStack(getArmor(1), 1);
		if(lchest.getType()!=Material.AIR) {
		ItemMeta i = lchest.getItemMeta();
		i.addEnchant(Enchantment.BINDING_CURSE, 100, true);
		i.addEnchant(Enchantment.VANISHING_CURSE, 100, true);
		i.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		lchest.setItemMeta(i);
		ee.setChestplate(lchest);
		}
		//helmet
		ItemStack lchest2 = new ItemStack(getArmor(0), 1);
		if(lchest2.getType()!=Material.AIR) {
		ItemMeta i = lchest2.getItemMeta();
		i.addEnchant(Enchantment.BINDING_CURSE, 100, true);
		i.addEnchant(Enchantment.VANISHING_CURSE, 100, true);
		i.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		lchest2.setItemMeta(i);
		ee.setHelmet(lchest2);
		}
		//leggings
		ItemStack lchest3 = new ItemStack(getArmor(2), 1);
		if(lchest3.getType()!=Material.AIR) {
		ItemMeta i = lchest3.getItemMeta();
		i.addEnchant(Enchantment.BINDING_CURSE, 100, true);
		i.addEnchant(Enchantment.VANISHING_CURSE, 100, true);
		i.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		lchest3.setItemMeta(i);
		ee.setLeggings(lchest3);
		}
		//boots
		ItemStack lchest4 = new ItemStack(getArmor(3), 1);
		if(lchest4.getType()!=Material.AIR) {
		ItemMeta i = lchest4.getItemMeta();
		i.addEnchant(Enchantment.BINDING_CURSE, 100, true);
		i.addEnchant(Enchantment.VANISHING_CURSE, 100, true);
		i.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		lchest4.setItemMeta(i);
		ee.setBoots(lchest4);
		}
		}
		catch(Exception error) {
		}
	}
	
	public static Material getArmor(int i) {
		if(i==0) {
			//helmet
			int rander = rand.nextInt(7);
			if(rander==0) {
				return Material.CHAINMAIL_HELMET;
			}
			else if(rander==1) {
				return Material.DIAMOND_HELMET;
			}
			else if(rander==2) {
				return Material.GOLDEN_HELMET;
			}
			else if(rander==3) {
				return Material.IRON_HELMET;
			}
			else if(rander==4) {
				return Material.LEATHER_HELMET;
			}
			else if(rander==5) {
				return Material.AIR;
			}
			else if(rander==6) {
				return Material.AIR;
			}
		}
		else if(i==1) {
			//chestplate
			int rander = rand.nextInt(7);
			if(rander==0) {
				return Material.CHAINMAIL_CHESTPLATE;
			}
			else if(rander==1) {
				return Material.DIAMOND_CHESTPLATE;
			}
			else if(rander==2) {
				return Material.GOLDEN_CHESTPLATE;
			}
			else if(rander==3) {
				return Material.IRON_CHESTPLATE;
			}
			else if(rander==4) {
				return Material.LEATHER_CHESTPLATE;
			}
			else if(rander==5) {
				return Material.AIR;
			}
			else if(rander==6) {
				return Material.AIR;
			}
		}
		else if(i==2) {
			//leggings
			int rander = rand.nextInt(7);
			if(rander==0) {
				return Material.CHAINMAIL_LEGGINGS;
			}
			else if(rander==1) {
				return Material.DIAMOND_LEGGINGS;
			}
			else if(rander==2) {
				return Material.GOLDEN_LEGGINGS;
			}
			else if(rander==3) {
				return Material.IRON_LEGGINGS;
			}
			else if(rander==4) {
				return Material.LEATHER_LEGGINGS;
			}
			else if(rander==5) {
				return Material.AIR;
			}
			else if(rander==6) {
				return Material.AIR;
			}
		}
		else if(i==3) {
			//boots
			int rander = rand.nextInt(7);
			if(rander==0) {
				return Material.CHAINMAIL_BOOTS;
			}
			else if(rander==1) {
				return Material.DIAMOND_BOOTS;
			}
			else if(rander==2) {
				return Material.GOLDEN_BOOTS;
			}
			else if(rander==3) {
				return Material.IRON_BOOTS;
			}
			else if(rander==4) {
				return Material.LEATHER_BOOTS;
			}
			else if(rander==5) {
				return Material.AIR;
			}
			else if(rander==6) {
				return Material.AIR;
			}
		}
		return null;
	}
	
}
