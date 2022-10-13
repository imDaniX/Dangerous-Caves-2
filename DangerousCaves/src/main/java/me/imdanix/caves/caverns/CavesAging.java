package me.imdanix.caves.caverns;

import io.papermc.lib.PaperLib;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.ticks.Tickable;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.bound.Bound;
import me.imdanix.caves.util.random.Rng;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class CavesAging implements Tickable, Configurable {
    private static final Set<Material> AGING_MATERIALS = new Materials.Builder(
            Material.COBBLESTONE, Material.STONE_BUTTON,
            Material.ANDESITE, Material.COBBLESTONE_WALL,
            Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.VINE
    ).build(true);

    private final Plugin plugin;
    private final Map<String, Set<Bound>> skippedChunks;
    private final Set<String> worlds;

    private boolean disabled;

    private Set<Material> replaceBlocks;
    private int lightLevel;
    private int radius;
    private int yMax;
    private double chance;
    private boolean chancePerChunks;
    private double agingChance;
    private int schedule;
    private boolean forceLoad;

    private double torchRemove;

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
        radius = Math.max(cfg.getInt("radius", 3), 1);
        yMax = Math.min(128, cfg.getInt("y-max", 80));
        chance = cfg.getDouble("chance", 50) / 100;
        chancePerChunks = cfg.getBoolean("use-chance-per-chunk", false);
        agingChance = cfg.getDouble("change-chance", 25) / 100;
        lightLevel = cfg.getInt("max-light-level", 0);
        if (lightLevel > 0) {
            lightLevelCheck = (b) -> {
                if (b.getLightFromBlocks() >= lightLevel) return false;
                for (BlockFace face : Locations.HORIZONTAL_FACES)
                    if (b.getRelative(face).getLightFromBlocks() >= lightLevel) return false;
                return true;
            };
        } else {
            lightLevelCheck = (b) -> true;
        }
        schedule = Math.max(cfg.getInt("schedule-timer", 4), 1);
        forceLoad = cfg.getBoolean("force-load", true);
        torchRemove = cfg.getDouble("torch-remove-chance", 40) / 100;
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);
        skippedChunks.clear();
        ConfigurationSection boundsCfg = cfg.getConfigurationSection("skip-chunks");
        if (boundsCfg != null)
            for (String worldStr : boundsCfg.getKeys(false)) {
                Set<Bound> worldBounds = new HashSet<>();
                for (String str : boundsCfg.getStringList(worldStr)) {
                    Bound bound = Bound.fromString(str);
                    if (bound != null) worldBounds.add(bound);
                }
                skippedChunks.put(worldStr, worldBounds);
            }

        replaceBlocks = Materials.getSet(cfg.getStringList("replace-blocks"));
        percentage = cfg.getDouble("percentage", 30) / 100;

        withReplace = cfg.getBoolean("age-types.replace", true);
        withRocks = cfg.getBoolean("age-types.rocks", true);
        withMushrooms = cfg.getBoolean("age-types.mushrooms", true);
        withVines = cfg.getBoolean("age-types.vines", true);

        disabled = !(cfg.getBoolean("enabled", true) && yMax > 0 && chance > 0 && agingChance > 0 &&
                percentage > 0 && lightLevel < 17 && !replaceBlocks.isEmpty() && !worlds.isEmpty() &&
                (withVines || withMushrooms || withRocks || withReplace));
    }

    @Override
    public void tick() {
        if (disabled) return;

        for (World world : Bukkit.getWorlds()) {
            if (!worlds.contains(world.getName())) continue;
            Set<QueuedChunk> chunks = new HashSet<>();
            for (Player player : world.getPlayers()) {
                if (!chancePerChunks && !Rng.chance(chance)) continue;
                Chunk start = player.getLocation().getChunk();
                // TODO: Move it to async too?
                for (int x = start.getX() - radius; x <= start.getX() + radius; x++) {
                    for (int z = start.getZ() - radius; z <= start.getZ() + radius; z++) {
                        if (isAllowed(world, x, z)) {
                            chunks.add(new QueuedChunk(x, z));
                        }
                    }
                }
            }
            proceedChunks(world.getUID(), chunks);
        }
    }

    private boolean isAllowed(World world, int x, int z) {
        Set<Bound> worldBounds = skippedChunks.get(world.getName());
        if (worldBounds == null) return true;
        for (Bound bound : worldBounds)
            if (bound.isInside(x, z)) return false;
        return true;
    }

    private void proceedChunks(UUID worldId, Set<QueuedChunk> chunks) {
        int timer = 0;
        for (QueuedChunk queuedChunk : chunks) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                World world = Bukkit.getWorld(worldId);
                if (world == null) return;
                Chunk chunk = queuedChunk.getChunk(world);
                if (!chunk.isLoaded()) {
                    if (forceLoad) {
                        PaperLib.getChunkAtAsync(world, queuedChunk.x, queuedChunk.z).thenAccept(
                                this::proceedChunk
                        );
                    }
                } else proceedChunk(chunk);
            }, (timer += schedule));
        }
    }

    private void proceedChunk(Chunk chunk) {
        Location edge = chunk.getBlock(0, 0, 0).getLocation();
        ChunkSnapshot snapshot = chunk.getChunkSnapshot(false, false, false);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (chancePerChunks && !Rng.chance(chance)) return;
            List<DelayedChange> changes = calculateChanges(edge, snapshot);
            if (changes.isEmpty()) return;

            Bukkit.getScheduler().runTask(plugin, () -> changes.forEach(change -> change.perform(chunk)));
        });
    }

    private List<DelayedChange> calculateChanges(Location edge, ChunkSnapshot snapshot) {
        List<DelayedChange> changes = new ArrayList<>();

        int count = 0;
        int totalCount = 0;
        // TODO Use world.getMinY()
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) for (int y = 2; y <= yMax; y++) {
            Material type = snapshot.getBlockType(x, y, z);

            if (type.isAir())
                continue;

            totalCount++;

            if (AGING_MATERIALS.contains(type))
                count++;

            if (snapshot.getBlockSkyLight(x, y, z) > 0)
                break;

            if (lightLevel > 0 && (snapshot.getBlockEmittedLight(x, y+1, z) >= lightLevel ||
                    snapshot.getBlockEmittedLight(x, y-1, z) >= lightLevel))
                continue;

            if (!Regions.INSTANCE.check(CheckType.BLOCK, Locations.add(edge, x, y, z)))
                continue;

            if (type == Material.TORCH) {
                if (torchRemove > 0 && Rng.chance(torchRemove))
                    changes.add(new DelayedChange(x, y, z, ChangeType.TORCH_AIR));
            } else if (replaceBlocks.contains(type) && Rng.chance(agingChance)) {
                if (withReplace) {
                    switch (Rng.nextInt(6)) {
                        case 0:
                            changes.add(new DelayedChange(x, y, z, ChangeType.ANDESITE));
                            break;

                        case 1:
                            changes.add(new DelayedChange(x, y, z, ChangeType.COBBLESTONE));
                            break;

                        case 2:
                            if (snapshot.getBlockType(x, y-1, z).isAir() && Rng.nextBoolean())
                                changes.add(new DelayedChange(x, y-1, z, ChangeType.STALAGMITE));
                            break;
                    }
                }

                if (withVines && Rng.chance(0.125)) {
                    changes.add(new DelayedChange(x, y, z, ChangeType.VINE));
                }

                if (snapshot.getBlockType(x, y+1, z).isAir()){
                    if (withMushrooms && Rng.chance(0.111)) {
                        changes.add(new DelayedChange(x, y+1, z, Rng.nextBoolean() ?
                                                                 ChangeType.RED_MUSHROOM : ChangeType.BROWN_MUSHROOM));
                    } else if (withRocks && Rng.chance(0.167)) {
                        changes.add(new DelayedChange(x, y+1, z, ChangeType.ROCK));
                    }
                }
            }
        }

        return count / (float)++totalCount > percentage ? Collections.emptyList() : changes;
    }

    @Override
    public TickLevel getTickLevel() {
        return TickLevel.WORLD;
    }

    @Override
    public String getConfigPath() {
        return "caverns.aging";
    }

    // TODO Make record too?
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
            if (!lightLevelCheck.test(block)) return;
            switch (changeType) {
                case VINE -> {
                    if (!replaceBlocks.contains(type)) return;
                    for (BlockFace face : Locations.HORIZONTAL_FACES) {
                        Block relBlock = block.getRelative(face);
                        if (relBlock.getType().isAir()) {
                            relBlock.setType(Material.VINE, false);
                            Materials.rotate(relBlock, face.getOppositeFace());
                        }
                    }
                }
                case RED_MUSHROOM -> {
                    if (!type.isAir() || !Materials.isCave(block.getRelative(BlockFace.DOWN).getType()))
                        return;
                    if (block.getLightLevel() > 12) return;
                    block.setType(Material.RED_MUSHROOM, false);
                }
                case BROWN_MUSHROOM -> {
                    if (!type.isAir() || !Materials.isCave(block.getRelative(BlockFace.DOWN).getType()))
                        return;
                    if (block.getLightLevel() > 12) return;
                    block.setType(Material.BROWN_MUSHROOM, false);
                }
                case ROCK -> {
                    if (!type.isAir() || !Materials.isCave(block.getRelative(BlockFace.DOWN).getType()))
                        return;
                    block.setType(Material.STONE_BUTTON, false);
                    Materials.rotate(block, BlockFace.UP);
                }
                case STALAGMITE -> {
                    if (!type.isAir()) return;
                    block.setType(Material.COBBLESTONE_WALL, false);
                }
                case COBBLESTONE -> {
                    if (!replaceBlocks.contains(type)) return;
                    block.setType(Material.COBBLESTONE, false);
                }
                case ANDESITE -> {
                    if (!replaceBlocks.contains(type)) return;
                    block.setType(Material.ANDESITE, false);
                }
                case TORCH_AIR -> {
                    if (type != Material.TORCH) return;
                    block.setType(Material.AIR, false);
                }
            }
        }
    }
    
    private enum ChangeType {
        VINE, RED_MUSHROOM, BROWN_MUSHROOM, ROCK, STALAGMITE, COBBLESTONE, ANDESITE, TORCH_AIR
    }

    private record QueuedChunk(int x, int z) {
        public Chunk getChunk(World world) {
            return world.getChunkAt(x, z);
        }
    }
}
