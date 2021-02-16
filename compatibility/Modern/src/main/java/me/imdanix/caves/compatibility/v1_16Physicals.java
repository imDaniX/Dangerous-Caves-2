package me.imdanix.caves.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class v1_16Physicals implements PhysicalsProvider {
    @Override
    public void rotate(Block block, BlockFace face) {
        BlockData data = block.getBlockData();
        if (data instanceof FaceAttachable) {
            ((FaceAttachable) data).setAttachedFace(FaceAttachable.AttachedFace.FLOOR);
        } else if (data instanceof Directional) {
            ((Directional) data).setFacing(face);
        } else if (data instanceof MultipleFacing) {
            ((MultipleFacing) data).setFace(face, true);
        }
        block.setBlockData(data, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    // TODO: For Paper use Profile API
    public ItemStack getHeadFromValue(String value) {
        UUID id = UUID.nameUUIDFromBytes(value.getBytes());
        // Heck yeah, magic numbers
        long less = id.getLeastSignificantBits();
        int lessA = (int) (less >> 32); int lessB = (int) less;
        long most = id.getMostSignificantBits();
        int mostA = (int) (most >> 32); int mostB = (int) most;
        return Bukkit.getUnsafe().modifyItemStack(
                new ItemStack(Material.PLAYER_HEAD),
                "{SkullOwner:{Id:[I;" + lessA + "," + lessB + "," + mostA + "," + mostB + "]," +
                        "Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }
}
