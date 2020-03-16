package com.github.evillootlye.caves.caverns;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.util.Locations;
import com.github.evillootlye.caves.util.Materials;
import com.github.evillootlye.caves.util.Utils;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

@Configurable.Path("caverns.ins")
public class CaveIns implements Listener, Configurable {
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 65, 3);

    private final Set<String> worlds;
    private double chance;
    private int y;
    private int radius;
    private boolean rabbitFoot;

    public CaveIns() {
        worlds = new HashSet<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("chance", 0.25) / 100;
        y = cfg.getInt("y-max", 25);
        radius = cfg.getInt("radius", 6);
        rabbitFoot = cfg.getBoolean("rabbit-foot", true);
        worlds.clear();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(chance <= 0) return;

        Block block = event.getBlock();
        World world = block.getWorld();
        if(block.getY() > y || !worlds.contains(world.getName()) ||
                !Materials.isCave(block.getType())) return;

        Player player = event.getPlayer();
        Location loc = player.getLocation();
        if(player.getGameMode() == GameMode.CREATIVE || !Locations.isCave(loc) ||
                (rabbitFoot && player.getInventory().contains(Material.RABBIT_FOOT))) return;

        if(Rnd.chance(chance)) {
            Location blockLoc = block.getLocation();
            world.playSound(blockLoc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            player.addPotionEffect(BLINDNESS);
            Locations.loop(radius, blockLoc, (l) -> {
                Block loopBlock = l.getBlock();
                if (loopBlock.getType() != Material.BEDROCK && Materials.isCave(loopBlock.getType())) {
                    world.spawnFallingBlock(l, block.getBlockData());
                    loopBlock.setType(Material.AIR);
                }
            });
        }
    }
}
