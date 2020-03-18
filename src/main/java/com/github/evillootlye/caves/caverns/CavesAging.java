package com.github.evillootlye.caves.caverns;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.ticks.TickLevel;
import com.github.evillootlye.caves.ticks.Tickable;
import com.github.evillootlye.caves.util.Materials;
import com.github.evillootlye.caves.util.Utils;
import com.github.evillootlye.caves.util.bound.Bound;
import com.github.evillootlye.caves.util.bound.DualBound;
import com.github.evillootlye.caves.util.bound.SingularBound;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.Switch;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class CavesAging implements Tickable, Configurable {
    private static final BlockFace[] FACES = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    private final Set<Bound> skippedChunks;
    private final Set<Material> replaceBlocks;
    private final Set<String> worlds;

    private int radius;
    private int y;
    private double chance;
    private double agingChance;

    public CavesAging() {
        skippedChunks = new HashSet<>();
        worlds = new HashSet<>();
        replaceBlocks = new HashSet<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        radius = cfg.getInt("radius", 3);
        y = Math.min(254, cfg.getInt("y-max", 80));
        chance = cfg.getDouble("chance", 50) / 100;
        agingChance = cfg.getDouble("change-chance", 25) / 100;
        skippedChunks.clear();
        for(String str : cfg.getStringList("skip-chunks")) {
            String[] xzStr = str.split(" ");
            String[] firstCoords = xzStr[0].split(",");
            if(xzStr.length > 1) {
                String[] secondCoords = xzStr[1].split(",");
                try {
                    Bound bound = new DualBound(
                            Integer.parseInt(firstCoords[0]), Integer.parseInt(secondCoords[0]),
                            Integer.parseInt(firstCoords[1]), Integer.parseInt(secondCoords[1])
                    );
                    skippedChunks.add(bound);
                } catch (NumberFormatException ignored) {}
            } else {
                try {
                    Bound bound = new SingularBound(
                            Integer.parseInt(firstCoords[0]), Integer.parseInt(firstCoords[1])
                    );
                    skippedChunks.add(bound);
                } catch (NumberFormatException ignored) {}
            }
        }
        worlds.clear();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);
        replaceBlocks.clear();
        for(String typeStr : cfg.getStringList("replace-blocks")) {
            Material type = Material.getMaterial(typeStr);
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
                Chunk start = player.getChunk();
                for(int x = start.getX() - radius; x <= start.getX() + radius; x++)
                for(int z = start.getZ() - radius; z <= start.getZ() + radius; z++)
                    if(isAllowed(x, z)) proceedChunk(world.getChunkAt(x, z));
            }
        }
    }

    private boolean isAllowed(int x, int z) {
//      return skippedChunks.contains(new SingularBound(x, z));
        for(Bound bound : skippedChunks)
            if(bound.isInside(x, z)) return false;
        return true;
    }

    private void proceedChunk(Chunk chunk) {
        for(int x = 0; x < 16; x++) for(int z = 0; z < 16; z++) for(int y = 2; y <= this.y; y++) {
            Block block = chunk.getBlock(x, y, z);
            if(block.getLightFromSky() > 0) break;
            Material type = block.getType();
            if(replaceBlocks.contains(type) && Rnd.chance(agingChance)) {
                switch(Rnd.nextInt(3)) {
                    case 0:
                        block.setType(Material.COBBLESTONE, false);
                        break;
                    case 1:
                        block.setType(Material.ANDESITE, false);
                        break;
                    default:
                        Block downBlock = block.getRelative(BlockFace.DOWN);
                        if(Materials.isAir(downBlock.getType()) && Rnd.nextBoolean())
                            downBlock.setType(Material.COBBLESTONE_WALL, false);
                }
                if(Rnd.chance(0.125)) {
                    for(BlockFace face : FACES) {
                        Block relBlock = block.getRelative(face);
                        if (Materials.isAir(relBlock.getType())) {
                            relBlock.setType(Material.VINE, false);
                            MultipleFacing facing = (MultipleFacing) relBlock.getBlockData();
                            facing.setFace(face.getOppositeFace(), true);
                            relBlock.setBlockData(facing);
                        }
                    }
                }
                Block upBlock = block.getRelative(BlockFace.UP);
                if(Materials.isAir(upBlock.getType())){
                    if(Rnd.chance(0.111)) {
                        upBlock.setType(Rnd.nextBoolean() ? Material.BROWN_MUSHROOM : Material.RED_MUSHROOM, false);
                    } else
                    if(Rnd.chance(0.167)) {
                        upBlock.setType(Material.STONE_BUTTON, false);
                        Switch button = (Switch) upBlock.getBlockData();
                        button.setFace(Switch.Face.FLOOR);
                        upBlock.setBlockData(button);
                    }
                }
            }
        }
    }

    @Override
    public TickLevel getTickLevel() {
        return TickLevel.WORLD;
    }
}
