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

package me.imdanix.caves.caverns;

import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.compatibility.VMaterial;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.ticks.Tickable;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.bound.Bound;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class CavesAging implements Tickable, Configurable {
    private static final EnumSet<Material> AGING_MATERIALS = EnumSet.of(
            Material.COBBLESTONE, Material.STONE_BUTTON,
            VMaterial.ANDESITE.get(), VMaterial.COBBLESTONE_WALL.get(),
            Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.VINE
    );

    private final Plugin plugin;
    private final Map<String, Set<Bound>> skippedChunks;
    private final Set<String> worlds;

    private Set<Material> replaceBlocks;
    private int lightLevel;
    private int radius;
    private int yMax;
    private double chance;
    private double agingChance;
    private int schedule;

    private double percentage;
    private Predicate<Block> lightLevelCheck;

    private boolean withVines;
    private boolean withRocks;
    private boolean withMushrooms;
    private boolean withReplace;

    public CavesAging(Plugin plugin) {
        this.plugin = plugin;
        skippedChunks = new HashMap<>();
        worlds = new HashSet<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        radius = cfg.getInt("radius", 3);
        yMax = Math.min(128, cfg.getInt("y-max", 80));
        chance = cfg.getDouble("chance", 50) / 100;
        agingChance = cfg.getDouble("change-chance", 25) / 100;
        lightLevel = cfg.getInt("max-light-level", 0);
        if(lightLevel > 0) {
            lightLevelCheck = (b) -> {
                if(b.getLightFromBlocks() >= lightLevel) return false;
                for(BlockFace face : Locations.HORIZONTAL_FACES)
                    if(b.getRelative(face).getLightFromBlocks() >= lightLevel) return false;
                return true;
            };
        } else {
            lightLevelCheck = (b) -> true;
        }
        schedule = Math.max(cfg.getInt("schedule-timer", 4), 1);
        worlds.clear();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);
        skippedChunks.clear();
        ConfigurationSection boundsCfg = cfg.getConfigurationSection("skip-chunks");
        if(boundsCfg != null)
            for(String worldStr : boundsCfg.getKeys(false)) {
                Set<Bound> worldBounds = new HashSet<>();
                for(String str : cfg.getStringList("skip-chunks")) {
                    Bound bound = Bound.fromString(str);
                    if(bound != null) worldBounds.add(bound);
                }
                skippedChunks.put(worldStr, worldBounds);
            }

        replaceBlocks = Materials.getEnumSet(cfg.getStringList("replace-blocks"));
        percentage = cfg.getDouble("percentage", 70) / 100;

        withReplace = cfg.getBoolean("age-types.replace", true);
        withRocks = cfg.getBoolean("age-types.rocks", true);
        withMushrooms = cfg.getBoolean("age-types.mushrooms", true);
        withVines = cfg.getBoolean("age-types.vines", true);
    }

    @Override
    public void tick() {
        if(chance <= 0) return;

        for(World world : Bukkit.getWorlds()) {
            if(!worlds.contains(world.getName())) continue;

            Set<QueuedChunk> chunks = new HashSet<>();
            for(Player player : world.getPlayers()) {
                if(!Rnd.chance(chance)) continue;
                Chunk start = player.getLocation().getChunk();
                for(int x = start.getX() - radius; x <= start.getX() + radius; x++)
                    for(int z = start.getZ() - radius; z <= start.getZ() + radius; z++)
                        if(isAllowed(world, x, z)) chunks.add(new QueuedChunk(x, z));
            }
            proceedChunks(new WeakReference<>(world), chunks);
        }
    }

    private boolean isAllowed(World world, int x, int z) {
        Set<Bound> worldBounds = skippedChunks.get(world.getName());
        if(worldBounds == null) return true;
        for(Bound bound : worldBounds)
            if(bound.isInside(x, z)) return false;
        return true;
    }

    private void proceedChunks(Reference<World> worldRef, Set<QueuedChunk> chunks) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        int timer = 0;
        for(QueuedChunk queuedChunk : chunks) {
            scheduler.runTaskLater(plugin, () -> {
                World world = worldRef.get();
                if(world == null) return;
                Chunk chunk = queuedChunk.getChunk(world);
                if(!chunk.isLoaded() && !chunk.load()) return;

                Location edge = chunk.getBlock(0, 0, 0).getLocation();
                ChunkSnapshot snapshot = chunk.getChunkSnapshot(false, false, false);
                scheduler.runTaskAsynchronously(plugin, () -> {
                    Set<DelayedChange> changes = calculateChanges(edge, snapshot);
                    if(changes.isEmpty()) return;

                    scheduler.runTask(plugin, () -> changes.forEach(change -> change.perform(chunk)));
                });
            }, (timer += schedule));
        }
    }

    private Set<DelayedChange> calculateChanges(Location edge, ChunkSnapshot snapshot) {
        Set<DelayedChange> changes = new HashSet<>();

        int count = 0;
        int totalCount = 0;
        for(int x = 0; x < 16; x++) for(int z = 0; z < 16; z++) for(int y = 2; y <= yMax; y++) {
            Material type = snapshot.getBlockType(x, y, z);

            if(Compatibility.isAir(type))
                continue;

            if(snapshot.getBlockSkyLight(x, y, z) > 0)
                break;

            totalCount++;

            Location current = edge.clone().add(x, 0, z);
            current.setY(y);
            if(lightLevel > 0 && (snapshot.getBlockEmittedLight(x, y+1, z) >= lightLevel ||
                    snapshot.getBlockEmittedLight(x, y-1, z) >= lightLevel) ||
                    Regions.INST.check(CheckType.BLOCK, current))
                continue;

            if(AGING_MATERIALS.contains(type))
                count++;

            if(replaceBlocks.contains(type) && Rnd.chance(agingChance)) {
                if(withReplace) {
                    switch(Rnd.nextInt(6)) {
                        case 0:
                            changes.add(new DelayedChange(x, y, z, ChangeType.ANDESITE));
                            break;

                        case 1:
                            changes.add(new DelayedChange(x, y, z, ChangeType.COBBLESTONE));
                            break;

                        case 2:
                            if(Compatibility.isAir(snapshot.getBlockType(x, y-1, z)) && Rnd.nextBoolean())
                                changes.add(new DelayedChange(x, y-1, z, ChangeType.STALAGMITE));
                            break;
                    }
                }

                if(withVines && Rnd.chance(0.125)) {
                    changes.add(new DelayedChange(x, y, z, ChangeType.VINE));
                }

                if(Compatibility.isAir(snapshot.getBlockType(x, y+1, z))){
                    if(withMushrooms && Rnd.chance(0.111)) {
                        changes.add(new DelayedChange(x, y+1, z, Rnd.nextBoolean() ?
                                ChangeType.RED_MUSHROOM : ChangeType.BROWN_MUSHROOM));
                    } else if(withRocks && Rnd.chance(0.167)) {
                        changes.add(new DelayedChange(x, y+1, z, ChangeType.ROCK));
                    }
                }
            }
        }

        return count / (float)++totalCount > percentage ? Collections.emptySet() : changes;
    }

    @Override
    public TickLevel getTickLevel() {
        return TickLevel.WORLD;
    }

    @Override
    public String getPath() {
        return "caverns.aging";
    }

    private class DelayedChange {
        private final int x;
        private final int y;
        private final int z;
        private final ChangeType changeType;
    
        public DelayedChange(int x, int y, int z, ChangeType changeType) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.changeType = changeType;
        }
    
        public void perform(Chunk chunk) {
            Block block = chunk.getBlock(x, y, z);
            Material type = block.getType();
            if(!lightLevelCheck.test(block)) return;
            switch (changeType) {
                case VINE:
                    if(!replaceBlocks.contains(type)) return;
                    for(BlockFace face : Locations.HORIZONTAL_FACES) {
                        Block relBlock = block.getRelative(face);
                        if (Compatibility.isAir(relBlock.getType())) {
                            relBlock.setType(Material.VINE, false);
                            Compatibility.rotate(relBlock, face.getOppositeFace());
                        }
                    }
                    break;

                case RED_MUSHROOM:
                    if(!Compatibility.isAir(type) || !Compatibility.isCave(block.getRelative(BlockFace.DOWN).getType())) return;
                    block.setType(Material.RED_MUSHROOM, false);
                    break;

                case BROWN_MUSHROOM:
                    if(!Compatibility.isAir(type) || !Compatibility.isCave(block.getRelative(BlockFace.DOWN).getType())) return;
                    block.setType(Material.BROWN_MUSHROOM, false);
                    break;

                case ROCK:
                    if(!Compatibility.isAir(type) || !Compatibility.isCave(block.getRelative(BlockFace.DOWN).getType())) return;
                    block.setType(Material.STONE_BUTTON, false);
                    Compatibility.rotate(block, BlockFace.UP);
                    break;

                case STALAGMITE:
                    if(!Compatibility.isAir(type)) return;
                    block.setType(VMaterial.COBBLESTONE_WALL.get(), false);
                    break;

                case COBBLESTONE:
                    if(!replaceBlocks.contains(type)) return;
                    block.setType(Material.COBBLESTONE, false);
                    break;

                case ANDESITE:
                    if(!replaceBlocks.contains(type)) return;
                    block.setType(VMaterial.ANDESITE.get(), false);
                    break;
            }
        }
    }
    
    private enum ChangeType {
        VINE, RED_MUSHROOM, BROWN_MUSHROOM, ROCK, STALAGMITE, COBBLESTONE, ANDESITE
    }

    private static class QueuedChunk {
        private final int x;
        private final int z;

        public QueuedChunk(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public Chunk getChunk(World world) {
            return world.getChunkAt(x, z);
        }

        @Override
        public int hashCode() {
            return (x >>> 15) * (z >>> 31) * 1907;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof QueuedChunk)) return false;
            QueuedChunk chunk = (QueuedChunk) o;
            return chunk.x == x && chunk.z == z;
        }
    }
}
