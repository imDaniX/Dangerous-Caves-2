package me.imdanix.caves.compatibility;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public interface MaterialsProvider {
    boolean isAir(Material type);

    boolean isCave(Material type);

    void rotate(Block block, BlockFace face);

    ItemStack getHeadFromValue(String value);
}
