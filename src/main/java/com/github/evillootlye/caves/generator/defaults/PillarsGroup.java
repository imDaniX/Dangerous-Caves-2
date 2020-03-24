package com.github.evillootlye.caves.generator.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.generator.StructureGroup;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

@Configurable.Path("generator.structures")
public class PillarsGroup extends StructureGroup implements Configurable {
    private int weight;

    public PillarsGroup() {
        super("pillars");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("pillars", 1);
    }

    @Override
    public void generate(Random random, Chunk chunk, Block block) {
        Location loc = block.getLocation();
        switch (random.nextInt(8)) {
            case 1:
                setType(loc, Material.POLISHED_ANDESITE);
                setType(loc.add(0, 1, 0), randomStone(random));
                setType(loc.add(0, 1, 0), Material.STONE_BRICK_SLAB);
                break;

            case 2:
                setType(loc, Material.STONE_BRICKS);
                if(random.nextBoolean()) {
                    setType(loc, Material.CRACKED_STONE_BRICKS);
                }
                setType(loc.add(0, 1, 0), Material.STONE_BRICKS);
                if(random.nextBoolean()) {
                    setType(loc, Material.CRACKED_STONE_BRICKS);
                }
                setType(loc.clone().add(random.nextInt(2), 0, 1), Material.STONE_BRICK_SLAB);
                setType(loc.add(0, 1, 0), Material.STONE_BRICK_SLAB);
                break;

            case 3:
                setType(loc, random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
                setType(loc.add(0, 1, 0), random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
                setType(loc.add(0, 1, 0), random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
                setType(loc.add(1, 0, random.nextInt(2)), Material.STONE_BRICK_SLAB);
                break;

            case 4:
                setType(loc, randomStone(random));
                setType(loc.clone().add(random.nextInt(2), 0, 1), Material.STONE_BRICK_SLAB);
                setType(loc.add(0,1,0), Material.POLISHED_ANDESITE);
                setType(loc.add(-1, 0, random.nextInt(2)), Material.STONE_BRICK_SLAB);
                break;

            case 5:
                setType(loc, Material.STONE_BRICKS);
                setType(loc.add(0, 1, 0), random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
                setType(loc.add(0, 1, 0), random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
                setType(loc.clone().add(0, 0, 1), Material.STONE_BRICK_SLAB);
                setType(loc.add(0, 1, 0), random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
                setType(loc.clone().subtract(0 , 0, 1), Material.STONE_BRICK_SLAB);
                setType(loc.add(0, 1, 0), Material.STONE_BRICK_SLAB);
                break;

            case 6:
                setType(loc, Material.STONE_BRICKS);
                setType(loc.add(0, 1, 0), random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
                setType(loc.clone().add(random.nextInt(2), 0, 1), Material.STONE_BRICK_SLAB);
                setType(loc.add(0, 1, 0), random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
                setType(loc.clone().add(1, 0, 0), Material.STONE_BRICK_SLAB);
                setType(loc.add(0, 1, 0), random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
                setType(loc.add(0, 1, 0), random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
                setType(loc.clone().add(1, 0 ,0), Material.STONE_BRICK_SLAB);
                setType(loc.add(0, 1, 0), random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
                setType(loc.clone().subtract(1, 0, 0), Material.STONE_BRICK_SLAB);
                setType(loc.add(0, 1, 0), Material.STONE_BRICK_SLAB);
                setType(loc.add(1, 0, 0), Material.STONE_BRICK_SLAB);
                break;

            case 7:
                setType(loc, Material.COBBLESTONE);
                setType(loc.add(0, 1, 0), Material.COBBLESTONE_WALL);
                break;

            default:
                setType(loc, Material.POLISHED_ANDESITE);
                setType(loc.add(0, 1, 0), Material.STONE_BRICK_SLAB);
                break;
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
