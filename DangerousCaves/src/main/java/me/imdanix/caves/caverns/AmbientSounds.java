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
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.ticks.Tickable;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AmbientSounds implements Tickable, Configurable {
    private boolean disabled;

    private final List<WrappedSound> sounds;
    private final Set<String> worlds;
    private double chance;
    private double radius;
    private int yMax;

    public AmbientSounds() {
        sounds = new ArrayList<>();
        worlds = new HashSet<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("chance", 25) / 100;
        yMax = cfg.getInt("y-max", 64);
        radius = cfg.getDouble("near", 7);
        sounds.clear();
        for (String soundStr : Configuration.section(cfg, "sounds").getKeys(false)) {
            Sound sound = Utils.getEnum(Sound.class, soundStr.toUpperCase(Locale.ENGLISH));
            if (sound == null) continue;
            sounds.add(new WrappedSound(
                    sound,
                    cfg.getDouble("volume", 1),
                    cfg.getDouble("pitch", 0.5)
            ));
        }
        if (sounds.isEmpty()) chance = 0;
        worlds.clear();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);

        disabled = !(cfg.getBoolean("enabled", true) && yMax > 0 && chance > 0 && !worlds.isEmpty());
    }

    @Override
    public void tick() {
        if (disabled) return;

        for (World world : Bukkit.getWorlds()) {
            if (!worlds.contains(world.getName())) continue;
            for (Player player : world.getPlayers()) {
                Location loc = player.getLocation();
                if (loc.getBlockY() <= yMax && Locations.isCave(loc) && Rnd.chance(chance)
                    && Regions.INSTANCE.check(CheckType.EFFECT, loc))
                    Rnd.randomElement(sounds).play(player);
            }
        }
    }

    @Override
    public TickLevel getTickLevel() {
        return TickLevel.PLAYER;
    }

    @Override
    public String getConfigPath() {
        return "caverns.ambient";
    }

    private class WrappedSound {
        private final Sound sound;
        private final float volume;
        private final float pitch;

        public WrappedSound(Sound sound, double volume, double pitch) {
            this.sound = sound;
            this.volume = (float) volume;
            this.pitch = (float) pitch;
        }

        public void play(Player player) {
            Location loc = player.getEyeLocation();
            if (radius > 0)
                loc.add(Rnd.nextDouble(-radius, radius),
                        Rnd.nextDouble(-radius, radius),
                        Rnd.nextDouble(-radius, radius));
            player.playSound(player.getEyeLocation(), sound, SoundCategory.AMBIENT, volume, pitch);
        }
    }
}
