package me.imdanix.caves.generator;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.util.Random;

public interface StructureGroup {

    /**
     * Get id of structure group
     * @return Id
     */
    String getId();

    /**
     * Generate a structure
     * @param random Generator's instance of random
     * @param chunk Current chunk
     * @param startBlock Block where AIR was found
     */
    void generate(Random random, Chunk chunk, Block startBlock);

    /**
     * Get group's weight in groups pool
     * @return Group's weight
     */
    int getWeight();

}
