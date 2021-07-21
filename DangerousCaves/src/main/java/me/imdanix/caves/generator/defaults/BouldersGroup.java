package me.imdanix.caves.generator.defaults;

import me.imdanix.caves.generator.AbstractStructure;
import me.imdanix.caves.util.Locations;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

public class BouldersGroup extends AbstractStructure {
    private int weight;

    public BouldersGroup() {
        super("boulders");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("boulders", 1);
    }

    @Override
    public void generate(Random random, Chunk chunk, Block block) {
        Location loc = block.getLocation();
        boolean[][][] rock = switch (random.nextInt(8)) {
            default -> OldStructures.rock1;
            case 1 -> OldStructures.rock2;
            case 2 -> OldStructures.rock3;
            case 3 -> OldStructures.rock4;
            case 4 -> OldStructures.rock5;
            case 5 -> OldStructures.rock6;
            case 6 -> OldStructures.rock7;
            case 7 -> OldStructures.rock8;
        };
        int xMod = random.nextBoolean() ? -1 : 1;
        int zMod = random.nextBoolean() ? -1 : 1;
        for (int y = 0; y < rock[0].length; y++) for (int x = -1; x < rock.length-1; x++) for (int z = -1; z < rock[0][0].length-1; z++) {
            if (rock[x + 1][y][z + 1]) {
                setType(Locations.add(loc, x * xMod, y, z * zMod), randomStone(random));
            }
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
