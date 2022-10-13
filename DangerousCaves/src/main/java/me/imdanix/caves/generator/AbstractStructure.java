package me.imdanix.caves.generator;

import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.util.random.Rng;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Configurable.Before("generator")
public abstract class AbstractStructure implements StructureGroup, Configurable {
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

    protected static Material randomStone(Random random) {
        return random.nextBoolean() ? Material.STONE : Material.COBBLESTONE;
    }

    protected static void setType(Location loc, Material material) {
        loc.getBlock().setType(material, false);
    }

    // TODO: It's actually kinda awful...

    protected static void fillInventory(Block block) {
        BlockState state;
        try {
            if (!((state = block.getState()) instanceof Container)) return;
        } catch (IllegalStateException ex) {
            // ItemsAdder goes somewhat wild?
            ex.printStackTrace();
            return;
        }
        if (mimicChance > 0 && Rng.chance(mimicChance)) {
            Compatibility.setTag(state, "mimic-30");
            return;
        }
        if (chestItems.isEmpty()) return;
        Inventory inventory = ((Container) state).getInventory();
        int itemsCount = Rng.nextInt(10) + 2;
        while (itemsCount-- > 0) {
            Material material = Rng.randomElement(chestItems);
            inventory.setItem(
                    Rng.nextInt(inventory.getSize()),
                    new ItemStack(material, material.getMaxStackSize() > 1 ? Rng.nextInt(3) + 1 : 1)
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
