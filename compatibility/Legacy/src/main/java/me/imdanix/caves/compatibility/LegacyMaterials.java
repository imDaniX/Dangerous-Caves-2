package me.imdanix.caves.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Vine;

import java.util.UUID;

public class LegacyMaterials implements MaterialsProvider {
    @Override
    public void rotate(Block block, BlockFace face) {
        BlockState state = block.getState();
        MaterialData data = state.getData();
        if (data instanceof Directional) {
            ((Directional) data).setFacingDirection(face);
        } else if (data instanceof Vine) {
            ((Vine) data).putOnFace(face);
        }
        state.update(false, false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getHeadFromValue(String value) {
        return Bukkit.getUnsafe().modifyItemStack(
                new ItemStack(Material.SKULL_ITEM, 1, (short) 3),
                "{SkullOwner:{Id:\"" + UUID.nameUUIDFromBytes(value.getBytes()) + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }
}

