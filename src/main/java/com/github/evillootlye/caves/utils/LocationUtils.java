package com.github.evillootlye.caves.utils;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.function.Consumer;

public class LocationUtils {
    public static void loop(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, World world, Consumer<Location> loop) {
        for(int x = xMin; x <= xMax; x++)
        for(int y = yMin; y <= yMax; y++)
        for(int z = zMin; z <= zMax; z++)
            loop.accept(new Location(world, x, y, z));
    }

    public static void loopRelative(int xOff, int yOff, int zOff, Location start, Consumer<Location> loop) {
        World world = start.getWorld();
        for(int x = start.getBlockX() - xOff; x <= start.getBlockX() + xOff; x++)
        for(int y = start.getBlockY() - yOff; y <= start.getBlockY() + yOff; y++)
        for(int z = start.getBlockZ() - zOff; z <= start.getBlockZ() + zOff; z++)
            loop.accept(new Location(world, x, y, z));
    }
}
