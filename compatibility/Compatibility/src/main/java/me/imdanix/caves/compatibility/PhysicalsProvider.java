package me.imdanix.caves.compatibility;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public interface PhysicalsProvider {
    void rotate(Block block, BlockFace face);

    ItemStack getHeadFromValue(String value);

    default int getYMin(World world) {
        return 0;
    }

    default int getYMax(World world) {
        return 256;
    }
}
