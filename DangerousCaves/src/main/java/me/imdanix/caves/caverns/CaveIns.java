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

import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.PseudoRandom;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CaveIns implements Listener, Configurable {
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 65, 3);
    private static final PseudoRandom PSEUDO_RANDOM = new PseudoRandom(new int[]{1, 0, -1, 0, 0, 1, 0, 1, 2, 0, 2, -1, 2, -1});

    private boolean disabled;

    private final Set<String> worlds;
    private double chance;
    private int yMax;
    private int radius;
    private boolean cuboid;
    private boolean slowFall;
    private boolean rabbitFoot;

    private boolean blastEffect;
    private boolean blastSound;

    private PseudoRandom pseudoRandom;
    private int distance;
    private int[][] heightMap;

    public CaveIns() {
        worlds = new HashSet<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("chance", 0.25) / 100;
        yMax = cfg.getInt("y-max", 25);
        radius = Math.max(cfg.getInt("radius", 6), 1);
        cuboid = cfg.getBoolean("cuboid", false);
        slowFall = cfg.getBoolean("slow-fall", false);
        rabbitFoot = cfg.getBoolean("rabbit-foot", true);

        blastEffect = cfg.getBoolean("blast-effect", true);
        blastSound = cfg.getBoolean("blast-sound", true);

        worlds.clear();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);

        pseudoRandom = cuboid ? PseudoRandom.ZERO_PSEUDO_RANDOM : PSEUDO_RANDOM;
        distance = radius*2 + 1;
        heightMap = calcMap();

        disabled = !(cfg.getBoolean("enabled", true) && yMax > 0 && chance > 0 && !worlds.isEmpty());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if (disabled) return;

        Block initBlock = event.getBlock();
        World world = initBlock.getWorld();
        if (initBlock.getY() > yMax || !worlds.contains(world.getName()) ||
                !Materials.isCave(initBlock.getType())) return;

        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || !Locations.isCave(player.getLocation()) ||
                (rabbitFoot && player.getInventory().contains(Material.RABBIT_FOOT))) return;

        if (Regions.INSTANCE.check(CheckType.BLOCK, initBlock.getLocation()) && Rnd.chance(chance)) {
            Location blockLoc = initBlock.getLocation();
            if (blastSound) world.playSound(blockLoc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            if (blastEffect) player.addPotionEffect(BLINDNESS);

            Set<List<Block>> allBlocks = getBlocksInRadius(blockLoc);

            if (slowFall) {
                allBlocks.forEach(l -> l.forEach(block -> {
                    world.spawnFallingBlock(block.getLocation(), block.getState().getData());
                    block.setType(Material.AIR);
                }));
            } else for (List<Block> blocks : allBlocks) {
                if (blocks.isEmpty()) continue;
                Block search = blocks.get(0).getRelative(BlockFace.DOWN);

                if (search.getType().isSolid()) continue;
                while(search.getY() > 0 && !search.getType().isSolid() && !(search = search.getRelative(BlockFace.DOWN)).getType().isSolid());

                for (Block block : blocks) {
                    search = search.getRelative(BlockFace.UP);
                    search.setType(block.getType());
                    block.setType(Material.AIR);
                }
            }
        }
    }

    private Set<List<Block>> getBlocksInRadius(Location blockLoc) {
        Set<List<Block>> allBlocks = new HashSet<>();

        blockLoc.subtract(radius, 2, radius);
        if (blockLoc.getY() < 1) blockLoc.setY(1);

        World world = blockLoc.getWorld();
        int yInit = blockLoc.getBlockY();

        for (int x = 0; x < distance; x++) for (int z = 0; z < distance; z++) {
            int xRel = blockLoc.getBlockX() + x;
            int zRel = blockLoc.getBlockZ() + z;

            List<Block> blocks = new ArrayList<>();

            int heightMax = heightMap[x][z] - pseudoRandom.next();
            for (int y = 0; y <= heightMax; y++) {
                Block block = world.getBlockAt(xRel, yInit + y, zRel);
                if (!Materials.isCave(block.getType())) {
                    if (Materials.isAir(block.getType())) continue;
                    break;
                }
                blocks.add(block);
            }

            allBlocks.add(blocks);
        }

        return allBlocks;
    }

    private int[][] calcMap() {
        int[][] heightMap = new int[distance][distance];
        for (int x = -radius; x <= radius; x++)
        for (int z = -radius; z <= radius; z++) {
            heightMap[x+radius][z+radius] = cuboid ?
                    distance :
                    (((-x*x) + (-z*z))/radius + radius*2);
        }

        return heightMap;
    }

    @Override
    public String getConfigPath() {
        return "caverns.ins";
    }

}
