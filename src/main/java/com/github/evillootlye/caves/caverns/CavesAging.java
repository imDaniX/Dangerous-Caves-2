package com.github.evillootlye.caves.caverns;

import com.github.evillootlye.caves.Dynamics;
import com.github.evillootlye.caves.configuration.Configurable;
import org.bukkit.configuration.ConfigurationSection;

public class CavesAging implements Dynamics.Tickable, Configurable {

    @Override
    public void reload(ConfigurationSection cfg) {

    }

    @Override
    public void tick() {

    }

    @Override
    public Dynamics.TickLevel getTickLevel() {
        return Dynamics.TickLevel.WORLD;
    }
}
