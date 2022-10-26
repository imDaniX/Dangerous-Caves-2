package me.imdanix.caves.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Consumer;

public final class Locations {
    public static final List<BlockFace> HORIZONTAL_FACES = List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    /**
     * Creates new Location instance and adds coordinates
     * @param loc Initial location
     * @param x X to add
     * @param y Y to add
     * @param z Z to add
     * @return Edited Location instance
     */
    public static Location add(Location loc, double x, double y, double z) {
        return new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
    }

    public static void loop(int radius, Location start, Consumer<Location> consumer) {
        loop(radius, start, ((world, x, y, z) -> consumer.accept(new Location(world, x, y, z))));
    }

    public static void loop(int radius, Location start, LocationConsumer consumer) {
        World world = start.getWorld();
        for (int x = start.getBlockX() - radius, xMax = start.getBlockX() + radius; x <= xMax; x++) {
            for (int y = start.getBlockY() - radius, yMax = start.getBlockY() + radius; y <= yMax; y++) {
                for (int z = start.getBlockZ() - radius, zMax = start.getBlockZ() + radius; z <= zMax; z++) {
                    consumer.accept(world, x, y, z);
                }
            }
        }
    }

    public static boolean isCave(Location loc) {
        Block block = loc.getBlock();
        Material lowerType;
        return block.getLightFromSky() <= 1 && (
                Materials.isCave(lowerType = block.getRelative(BlockFace.DOWN).getType()) ||
                lowerType.isAir());

    }

    public static boolean isLookingAt(LivingEntity viewer, LivingEntity target) {
        Location eye = viewer.getEyeLocation();
        Vector toEntity = target.getEyeLocation().toVector().subtract(eye.toVector());
        return toEntity.normalize().dot(eye.getDirection()) > 0.70D;
    }

    public static void playSound(Location loc, Sound sound, SoundCategory category, float volume, float pitch) {
        loc.getWorld().playSound(loc, sound, category, volume, pitch);
    }

    public static void playSound(Location loc, Sound sound, float volume, float pitch) {
        loc.getWorld().playSound(loc, sound, volume, pitch);
    }

    @FunctionalInterface
    public interface LocationConsumer {
        void accept(World w, int x, int y, int z);
    }
}
