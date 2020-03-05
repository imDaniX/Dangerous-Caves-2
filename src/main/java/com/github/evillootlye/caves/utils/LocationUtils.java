package com.github.evillootlye.caves.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class LocationUtils {
    public static void loop(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, World world, Consumer<Location> loop) {
        for(int x = xMin; x <= xMax; x++)
        for(int y = yMin; y <= yMax; y++)
        for(int z = zMin; z <= zMax; z++)
            loop.accept(new Location(world, x, y, z));
    }

    public static void loopRelative(int xRadius, int yRadius, int zRadius, Location start, Consumer<Location> loop) {
        World world = start.getWorld();
        for(int x = start.getBlockX() - xRadius; x <= start.getBlockX() + xRadius; x++)
        for(int y = start.getBlockY() - yRadius; y <= start.getBlockY() + yRadius; y++)
        for(int z = start.getBlockZ() - zRadius; z <= start.getBlockZ() + zRadius; z++)
            loop.accept(new Location(world, x, y, z));
    }

    public static boolean isCave(Location loc) {
        return loc.getBlock().getLightFromSky() < 1 &&
                MaterialUtils.CAVE.contains(loc.subtract( 0, 1, 0).getBlock().getType());

    }

    public static boolean isLookingAt(LivingEntity viewer, LivingEntity target) {
        Location eye = viewer.getEyeLocation();
        Vector toEntity = target.getEyeLocation().toVector().subtract(eye.toVector());
        return toEntity.normalize().dot(eye.getDirection()) > 0.70D;
    }
}
