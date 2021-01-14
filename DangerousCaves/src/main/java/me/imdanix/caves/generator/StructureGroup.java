/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2021  imDaniX
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
