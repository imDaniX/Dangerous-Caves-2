package com.github.evillootlye.caves;

import com.github.evillootlye.caves.configuration.Configuration;
import com.github.evillootlye.caves.generator.CaveGenerator;
import com.github.evillootlye.caves.mobs.DefaultMobs;
import com.github.evillootlye.caves.mobs.MobsManager;
import com.github.evillootlye.caves.surroundings.AmbientSounds;
import org.bukkit.plugin.java.JavaPlugin;

public class DangerousCaves extends JavaPlugin {
    public static DangerousCaves INSTANCE;

    private MobsManager mobsManager;

    @Override
    @SuppressWarnings("deprecation")
    public void onEnable() {
        DangerousCaves.INSTANCE = this;

        Dynamics dynamics = new Dynamics();
        Configuration cfg = new Configuration("config"); cfg.create(true);

        mobsManager = new MobsManager(cfg);
        DefaultMobs.registerAll(mobsManager);
        AmbientSounds ambient = new AmbientSounds();

        dynamics.subscribe(ambient);
        dynamics.subscribe(mobsManager);

        cfg.register(new CaveGenerator());
        cfg.register(ambient);

        new DangerousCavesOld().onEnable();
    }

    public MobsManager getMobs() {
        return mobsManager;
    }
}
