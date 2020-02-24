package com.github.evillootlye.caves.surroundings;

import com.github.evillootlye.caves.Dynamics;
import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.configuration.Configuration;
import com.github.evillootlye.caves.utils.LocationUtils;
import com.github.evillootlye.caves.utils.Rnd;
import com.github.evillootlye.caves.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configurable.Path("caves.ambient")
public class AmbientSounds implements Dynamics.Tickable, Configurable {
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
                if(LocationUtils.isCave(player.getLocation(), y) && chance > Rnd.nextDouble())
                    sounds.get(Rnd.nextInt(sounds.size())).play(player);
            }
        }
    }

    @Override
    public Dynamics.TickLevel getTickLevel() {
        return Dynamics.TickLevel.WORLD;
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
            player.playSound(player.getLocation(), sound, SoundCategory.AMBIENT, volume, pitch);
        }
    }
}
