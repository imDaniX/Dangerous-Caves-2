package me.imdanix.caves.generator;

import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.util.Manager;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.WeightedPool;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/*
    TODO: Redo from scratch
 */
public class CaveGenerator extends BlockPopulator implements Manager<StructureGroup>, Configurable {
    private final Configuration config;

    private boolean disabled;

    private double chance;
    private int maxTries;

    private final Map<String, StructureGroup> structures;
    private WeightedPool<StructureGroup> structuresPool;

    public CaveGenerator(Configuration config) {
        this.config = config;
        this.structures = new HashMap<>();
        this.structuresPool = new WeightedPool<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("chance", 50) / 100;
        maxTries = cfg.getInt("max-tries", 3);

        AbstractStructure.setMimicChance(cfg.getDouble("mimic-chance") / 100);

        Set<String> worlds = new HashSet<>();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);

        disabled = !(cfg.getBoolean("enabled", true) && chance > 0 && maxTries > 0 && !worlds.isEmpty());
        if (!disabled) recalculate();

        for (World world : Bukkit.getWorlds()) {
            List<BlockPopulator> populators = world.getPopulators();
            populators.remove(this);
            if (!disabled && worlds.contains(world.getName())) populators.add(this);
        }
        List<Material> items = new ArrayList<>();

        List<String> itemsCfg = cfg.getStringList("chest-items");
        for (String materialStr : itemsCfg) {
            Material material = Material.getMaterial(materialStr.toUpperCase(Locale.ROOT));
            if (material != null) items.add(material);
        }
        AbstractStructure.setItems(items);
    }

    /**
     * Recalculate structure groups spawn chances
     */
    private void recalculate() {
        structuresPool = new WeightedPool<>();
        structures.values().forEach(g -> structuresPool.add(g, g.getWeight()));
        if (structuresPool.isEmpty()) {
            disabled = true;
        }
    }

    /**
     * Register a new structure group
     * @param group Group to register
     */
    @Override
    public boolean register(StructureGroup group) {
        if (structures.containsKey(group.getId())) return false;
        structures.put(group.getId(), group);
        structuresPool.add(group, group.getWeight());
        if (group instanceof Configurable)
            config.register((Configurable) group);
        return true;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if (chance > random.nextDouble()) {
            Block block = null;
            int tries = 0;
            while (tries++ < maxTries && block == null) {
                block = getClosestAir(chunk, random.nextInt(16), random.nextInt(16));
            }
            if (block != null) structuresPool.next().generate(random, chunk, block);
        }
    }

    @Override
    public String getConfigPath() {
        return "generator";
    }

    private static Block getClosestAir(Chunk chunk, int x, int z) {
        for (int y = chunk.getWorld().getMinHeight(); y < 55; y++) {
            Block block = chunk.getBlock(x, y, z);
            if (block.getType().isAir()
                    && block.getRelative(BlockFace.UP).getType().isAir()
                    && Materials.isCave(block.getRelative(BlockFace.DOWN).getType()))
                return block;
        }
        return null;
    }
}
