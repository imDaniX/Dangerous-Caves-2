package me.imdanix.caves.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class FlattenedMaterials implements MaterialsProvider {

    private final Set<Material> CAVE = Collections.unmodifiableSet(EnumSet.of(
            Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE,
            Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.COAL_ORE,
            Material.COBBLESTONE, Material.MOSSY_COBBLESTONE,
            Material.DIRT, Material.GRAVEL, Material.OBSIDIAN, Material.OAK_PLANKS, Material.BEDROCK,
            Material.SOUL_SAND, Material.NETHERRACK, Material.NETHER_BRICKS
    ));

    private final Set<Material> AIR = Collections.unmodifiableSet(EnumSet.of(
            Material.AIR, Material.CAVE_AIR, Material.VOID_AIR
    ));

    @Override
    public boolean isAir(Material type) {
        return AIR.contains(type);
    }

    @Override
    public boolean isCave(Material type) {
        return CAVE.contains(type);
    }

    @Override
    public void rotate(Block block, BlockFace face) {
        BlockData data = block.getBlockData();
        if(data instanceof Directional) {
            ((Directional) data).setFacing(face);
        } else if(data instanceof MultipleFacing) {
            ((MultipleFacing) data).setFace(face, true);
        }
        block.setBlockData(data, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getHeadFromValue(String value) {
        return Bukkit.getUnsafe().modifyItemStack(
                new ItemStack(Material.PLAYER_HEAD),
                "{SkullOwner:{Id:\"" + UUID.nameUUIDFromBytes(value.getBytes()) + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }
}
