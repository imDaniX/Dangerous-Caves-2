package com.github.evillootlye.caves.generator.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.generator.StructureGroup;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

@Configurable.Path("generator.structures")
public class TrapsGroup extends StructureGroup implements Configurable {
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
                setType(loc, Material.STONE_PRESSURE_PLATE);
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
                setType(loc, Material.STONE_PRESSURE_PLATE);
                setType(loc.subtract(0, 1, 0), Material.GRAVEL);
                setType(loc.subtract(0, 1, 0), Material.TNT);
                while(loc.getY()>4) {
                    setType(loc.subtract(0, 1, 0), Material.AIR);
                }
                setType(loc.add(0, 1, 0), Material.COBWEB);
                setType(loc.add(0, 1, 0), Material.COBWEB);
                setType(loc.add(0, 1, 0), Material.COBWEB);
                break;

            case 4:
                setType(loc, Material.STONE_PRESSURE_PLATE);
                setType(loc.subtract(0, 1, 0), Material.GRAVEL);
                setType(loc.subtract(0, 1, 0), Material.TNT);
                while(loc.getY()>4) {
                    setType(loc.subtract(0, 1, 0), Material.AIR);
                }
                break;

            case 5:
                setType(loc, Material.STONE_PRESSURE_PLATE);
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
                setType(loc, Material.STONE_PRESSURE_PLATE);
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
                setType(loc, Material.STONE_PRESSURE_PLATE);
                setType(loc.subtract(0, 1, 0), Material.DISPENSER);
                ((Dispenser) loc.getBlock().getState()).getInventory().addItem(new ItemStack(Material.ARROW, rnd.nextInt(3) + 1));
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
