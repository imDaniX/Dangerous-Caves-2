/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.caves.util;

import me.imdanix.caves.compatibility.Compatibility;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
