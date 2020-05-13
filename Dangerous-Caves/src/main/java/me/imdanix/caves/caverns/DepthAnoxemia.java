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
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.ticks.Tickable;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DepthAnoxemia implements Tickable, Configurable {
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, 120, 1);
    private static final PotionEffect SLOW_DIGGING = new PotionEffect(PotionEffectType.SLOW_DIGGING, 55, 1);

    private final Set<String> worlds;

    private List<String> messages;
    private boolean actionbar;
    private double chance;
    private int yMax;

    public DepthAnoxemia() {
        worlds = new HashSet<>();
        messages = new ArrayList<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("try-chance", 0.8) / 100;
        yMax = cfg.getInt("y-max", 42);
        actionbar = cfg.getBoolean("actionbar", true);
        messages.clear();
        messages.addAll(Utils.clr(cfg.getStringList("messages")));
        worlds.clear();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);
    }

    @Override
    public void tick() {
        if(chance <= 0 || yMax <= 0) return;
        for(World world : Bukkit.getWorlds()) {
            if (!worlds.contains(world.getName())) continue;
            for (Player player : world.getPlayers()) {
                if(player.getLocation().getY() > yMax || !Rnd.chance(chance)) continue;
                if(!checkChance(player)) continue;

                if(actionbar)
                    Compatibility.sendActionBar(player, Rnd.randomItem(messages));
                else
                    player.sendMessage(Rnd.randomItem(messages));

                player.addPotionEffect(SLOW);
                player.addPotionEffect(SLOW_DIGGING);
            }
        }
    }

    private boolean checkChance(Player player) {
        double depthChance = (yMax - player.getLocation().getY()) / yMax;
        double weightChance = 0;
        ItemStack[] contents = player.getInventory().getContents();
        for(ItemStack item : contents) {
            if(item == null) continue;
            weightChance += item.getAmount() / item.getMaxStackSize();
        }
        weightChance /= contents.length;
        return Rnd.chance((depthChance + weightChance) / 2);
    }

    @Override
    public TickLevel getTickLevel() {
        return TickLevel.PLAYER;
    }

    @Override
    public String getPath() {
        return "caverns.anoxemia";
    }
}
