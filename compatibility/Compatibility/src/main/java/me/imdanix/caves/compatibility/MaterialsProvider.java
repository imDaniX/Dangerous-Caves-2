package me.imdanix.caves.compatibility;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public interface MaterialsProvider {
    void rotate(Block block, BlockFace face);

    ItemStack getHeadFromValue(String value);
}
