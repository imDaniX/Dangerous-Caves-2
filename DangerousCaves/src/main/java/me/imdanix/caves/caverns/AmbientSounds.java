package me.imdanix.caves.caverns;

import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.ticks.Tickable;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rng;
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
    private boolean worldSound;
    private double worldDistance;

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
            Sound sound = Utils.getEnum(Sound.class, soundStr.toUpperCase(Locale.ROOT));
            if (sound == null) continue;
            sounds.add(new WrappedSound(
                    sound,
                    cfg.getDouble("volume", 1),
                    cfg.getDouble("pitch", 0.5)
            ));
        }
        if (sounds.isEmpty()) chance = 0;
        worldSound = cfg.getBoolean("server-wise", true);
        worldDistance = cfg.getDouble("server-wise-distance", 0); worldDistance *= worldDistance;
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);

        disabled = !cfg.getBoolean("enabled", true);
    }

    @Override
    public void tick() {
        if (disabled) return;
        List<Location> soundSources = worldSound && worldDistance > 0 ? new ArrayList<>() : null;
        for (World world : Bukkit.getWorlds()) {
            if (!worlds.contains(world.getName())) continue;
            for (Player player : world.getPlayers()) {
                Location loc = player.getLocation();
                if (loc.getBlockY() > yMax || !Locations.isCave(loc) || !Rng.chance(chance) ||
                        !Regions.INSTANCE.check(CheckType.EFFECT, loc)) continue;
                if (worldSound && soundSources != null) {
                    boolean check = false;
                    for (Location source : soundSources) {
                        if (source.distanceSquared(loc) > worldDistance) continue;
                        check = true;
                        break;
                    }
                    if (check) continue;
                    soundSources.add(loc);
                }
                Rng.randomElement(sounds).play(player, worldSound);
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

        public void play(Player player, boolean world) {
            Location loc = player.getEyeLocation();
            if (radius > 0)
                loc.add(Rng.nextDouble(-radius, radius),
                        Rng.nextDouble(-radius, radius),
                        Rng.nextDouble(-radius, radius));
            if (world) {
                loc.getWorld().playSound(loc, sound, SoundCategory.AMBIENT, volume, pitch);
            } else {
                player.playSound(loc, sound, SoundCategory.AMBIENT, volume, pitch);
            }
        }
    }
}
