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

package me.imdanix.caves.generator;

import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class AbstractStructure implements StructureGroup {
    private static final List<Material> chestItems = new ArrayList<>();
    private static double mimicChance;

    private final String id;

    public AbstractStructure(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getConfigPath() {
        return "generator.structures";
    }

    public static Material randomStone(Random random) {
        return random.nextBoolean() ? Material.STONE : Material.COBBLESTONE;
    }

    public static void setType(Location loc, Material material) {
        loc.getBlock().setType(material, false);
    }

    public static void fillInventory(Block block) {
        if (!(block.getState() instanceof Container)) return;
        if (mimicChance > 0 && Rnd.chance(mimicChance)) {
            Compatibility.setTag(block, "mimic-30");
            return;
        }
        if (chestItems.isEmpty()) return;
        Inventory inventory = ((Container)block.getState()).getInventory();
        int itemsCount = Rnd.nextInt(10) + 2;
        while(itemsCount-- > 0) {
            Material material = Rnd.randomElement(chestItems);
            inventory.setItem(
                    Rnd.nextInt(inventory.getSize()),
                    new ItemStack(material, material.getMaxStackSize() > 1 ? Rnd.nextInt(3) + 1 : 1)
            );
        }
    }

    public static void setItems(Collection<Material> chestItems) {
        AbstractStructure.chestItems.clear();
        AbstractStructure.chestItems.addAll(chestItems);
    }

    public static void setMimicChance(double chance) {
        mimicChance = chance;
    }
}
