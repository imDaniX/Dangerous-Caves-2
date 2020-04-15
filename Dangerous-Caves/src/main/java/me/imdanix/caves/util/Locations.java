package me.imdanix.caves.util;

import me.imdanix.caves.compatibility.Compatibility;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public final class Locations {
    public static final BlockFace[] HORIZONTAL_FACES = {BlockFace.NORTH,BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST};
    public static final BlockFace[] FULL_FACES = {BlockFace.NORTH,BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST,BlockFace.UP,BlockFace.DOWN};

    public static void loop(int radius, Location start, Consumer<Location> loop) {
        World world = start.getWorld();
        for(int x = start.getBlockX() - radius; x <= start.getBlockX() + radius; x++)
        for(int y = start.getBlockY() - radius; y <= start.getBlockY() + radius; y++)
        for(int z = start.getBlockZ() - radius; z <= start.getBlockZ() + radius; z++)
            loop.accept(new Location(world, x, y, z));
    }

    public static void loop(int radius, Location start, LocationConsumer loop) {
        World world = start.getWorld();
        for(int x = start.getBlockX() - radius; x <= start.getBlockX() + radius; x++)
        for(int y = start.getBlockY() - radius; y <= start.getBlockY() + radius; y++)
        for(int z = start.getBlockZ() - radius; z <= start.getBlockZ() + radius; z++)
            loop.accept(world, x, y, z);
    }

    public static boolean isCave(Location loc) {
        Block block = loc.getBlock();
        return block.getLightFromSky() == 0 &&
                Compatibility.isCave(block.getRelative(BlockFace.DOWN).getType());

    }

    public static boolean isLookingAt(LivingEntity viewer, LivingEntity target) {
        Location eye = viewer.getEyeLocation();
        Vector toEntity = target.getEyeLocation().toVector().subtract(eye.toVector());
        return toEntity.normalize().dot(eye.getDirection()) > 0.70D;
    }

    @FunctionalInterface
    public interface LocationConsumer {
        void accept(World w, int x, int y, int z);
    }
}
