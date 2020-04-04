package me.imdanix.caves.caverns;

import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.configuration.Configuration;
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
import java.util.Set;

@Configurable.Path("caverns.ambient")
public class AmbientSounds implements Tickable, Configurable {
    private final List<WrappedSound> sounds;
    private final Set<String> worlds;
    private double chance;
    private int y;

    public AmbientSounds() {
        sounds = new ArrayList<>();
        worlds = new HashSet<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("chance", 25) / 100;
        y = cfg.getInt("y-max", 64);
        sounds.clear();
        for(String soundStr : Configuration.section(cfg, "sounds").getKeys(false)) {
            Sound sound = Utils.getEnum(Sound.class, soundStr.toUpperCase());
            if(sound == null) continue;
            sounds.add(new WrappedSound(
                    sound,
                    cfg.getDouble("volume", 1),
                    cfg.getDouble("pitch", 0.5)
            ));
        }
        worlds.clear();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);
    }

    @Override
    public void tick() {
        if(chance <= 0) return;
        for(World world : Bukkit.getWorlds()) {
            if(!worlds.contains(world.getName())) continue;
            for(Player player : world.getPlayers()) {
                Location loc = player.getLocation();
                if(loc.getBlockY() <= y && Locations.isCave(loc) && Rnd.chance(chance))
                    Rnd.randomItem(sounds).play(player);
            }
        }
    }

    @Override
    public TickLevel getTickLevel() {
        return TickLevel.WORLD;
    }

    private static class WrappedSound {
        private final Sound sound;
        private final float volume;
        private final float pitch;

        private WrappedSound(Sound sound, double volume, double pitch) {
            this.sound = sound;
            this.volume = (float) volume;
            this.pitch = (float) pitch;
        }

        public void play(Player player) {
            player.playSound(player.getEyeLocation(), sound, SoundCategory.AMBIENT, volume, pitch);
        }
    }
}
