package me.imdanix.caves.caverns;

import me.imdanix.caves.DangerousCaves;
import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.compatibility.VMaterial;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.ticks.Tickable;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.bound.Bound;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Configurable.Path("caverns.aging")
public class CavesAging implements Tickable, Configurable {
    private static final BlockFace[] REAL_FACES = {BlockFace.NORTH,BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST,BlockFace.UP,BlockFace.DOWN};
    private final Map<String, Set<Bound>> skippedChunks;
    private final Set<Material> replaceBlocks;
    private final Set<String> worlds;
    private final Set<QueuedChunk> chunks;

    private int radius;
    private int y;
    private double chance;
    private double agingChance;
    private Predicate<Block> lightLevelCheck;
    private int schedule;

    public CavesAging() {
        skippedChunks = new HashMap<>();
        worlds = new HashSet<>();
        replaceBlocks = new HashSet<>();
        chunks = new HashSet<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        radius = cfg.getInt("radius", 3);
        y = Math.min(254, cfg.getInt("y-max", 80));
        chance = cfg.getDouble("chance", 50) / 100;
        agingChance = cfg.getDouble("change-chance", 25) / 100;
        int lightLevel = cfg.getInt("max-light-level", 0);
        if(lightLevel > 0) {
            lightLevelCheck = (b) -> {
                for(BlockFace face : REAL_FACES)
                    if(b.getRelative(face).getLightFromBlocks() >= lightLevel) return false;
                return true;
            };
        } else {
            lightLevelCheck = (b) -> true;
        }
        schedule = Math.max(cfg.getInt("schedule-timer", 0), 0);
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
        replaceBlocks.clear();
        for(String typeStr : cfg.getStringList("replace-blocks")) {
            Material type = Material.getMaterial(typeStr.toUpperCase());
            if(type == null) continue;
            replaceBlocks.add(type);
        }
    }

    @Override
    public void tick() {
        if(chance <= 0) return;
        for(World world : Bukkit.getWorlds()) {
            if(!worlds.contains(world.getName())) continue;
            for(Player player : world.getPlayers()) {
                if(!Rnd.chance(chance)) continue;
                Chunk start = player.getLocation().getChunk();
                for(int x = start.getX() - radius; x <= start.getX() + radius; x++)
                for(int z = start.getZ() - radius; z <= start.getZ() + radius; z++)
                    if(isAllowed(world, x, z)) chunks.add(new QueuedChunk(world, x, z));
            }
        }
        if(schedule > 0) {
            int timer = 2;
            BukkitScheduler scheduler = Bukkit.getScheduler();
            for(QueuedChunk queuedChunk : chunks) {
                scheduler.runTaskLater(DangerousCaves.PLUGIN,
                        () -> queuedChunk.doStuff(this::proceedChunk),
                        timer
                );
                timer += 2;
            }
        } else {
            chunks.forEach(q -> q.doStuff(this::proceedChunk));
        }
        chunks.clear();
    }

    private boolean isAllowed(World world, int x, int z) {
        Set<Bound> worldBounds = skippedChunks.get(world.getName());
        if(worldBounds == null) return true;
        for(Bound bound : worldBounds)
            if(bound.isInside(x, z)) return false;
        return true;
    }

    private void proceedChunk(Chunk chunk) {
        for(int x = 0; x < 16; x++) for(int z = 0; z < 16; z++) for(int y = 2; y <= this.y; y++) {
            Block block = chunk.getBlock(x, y, z);
            if(block.getLightFromSky() > 0) break;
            Material type = block.getType();
            if(replaceBlocks.contains(type) && Rnd.chance(agingChance) && lightLevelCheck.test(block)) {
                if(Rnd.nextBoolean())
                    switch(Rnd.nextInt(3)) {
                        case 0:
                            block.setType(Material.COBBLESTONE, false);
                            break;

                        case 1:
                            block.setType(VMaterial.ANDESITE.get(), false);
                            break;

                        default:
                            Block downBlock = block.getRelative(BlockFace.DOWN);
                            if(Compatibility.isAir(downBlock.getType()) && Rnd.nextBoolean())
                                downBlock.setType(VMaterial.COBBLESTONE_WALL.get(), false);
                            break;
                    }
                if(Rnd.chance(0.125)) {
                    for(BlockFace face : Locations.HORIZONTAL_FACES) {
                        Block relBlock = block.getRelative(face);
                        if (Compatibility.isAir(relBlock.getType())) {
                            relBlock.setType(Material.VINE, false);
                            Compatibility.rotate(relBlock, face.getOppositeFace());
                        }
                    }
                }
                Block upBlock = block.getRelative(BlockFace.UP);
                if(Compatibility.isAir(upBlock.getType())){
                    if(Rnd.chance(0.111)) {
                        upBlock.setType(Rnd.nextBoolean() ? Material.BROWN_MUSHROOM : Material.RED_MUSHROOM, false);
                    } else if(Rnd.chance(0.167)) {
                        upBlock.setType(Material.STONE_BUTTON, false);
                        Compatibility.rotate(upBlock, BlockFace.UP);
                    }
                }
            }
        }
    }

    @Override
    public TickLevel getTickLevel() {
        return TickLevel.WORLD;
    }

    private static class QueuedChunk {
        private final Reference<World> world;
        private final int x;
        private final int z;

        private QueuedChunk(World world, int x, int z) {
            this.world = new WeakReference<>(world);
            this.x = x;
            this.z = z;
        }

        private void doStuff(Consumer<Chunk> run) {
            World world = this.world.get();
            if(world != null) run.accept(world.getChunkAt(x, z));
        }

        @Override
        public int hashCode() {
            return (x >>> 15) * (z >>> 31) * world.hashCode() * 1907;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof QueuedChunk)) return false;
            QueuedChunk chunk = (QueuedChunk) o;
            return chunk.x == x && chunk.z == z && chunk.world == world;
        }
    }
}
