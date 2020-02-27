package com.github.evillootlye.caves;

import com.github.evillootlye.caves.caverns.AmbientSounds;
import com.github.evillootlye.caves.caverns.CaveInsPlayerListener;
import com.github.evillootlye.caves.configuration.Configuration;
import com.github.evillootlye.caves.generator.CaveGenerator;
import com.github.evillootlye.caves.mobs.DefaultMobs;
import com.github.evillootlye.caves.mobs.MobsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DangerousCaves extends JavaPlugin {
    public static DangerousCaves INSTANCE;

    private MobsManager mobsManager;

    @Override
    @SuppressWarnings("deprecation")
    public void onEnable() {
        DangerousCaves.INSTANCE = this;

        Dynamics dynamics = new Dynamics(this);
        Configuration cfg = new Configuration(this, "config"); cfg.create(true);

        mobsManager = new MobsManager(this, cfg);
        DefaultMobs.registerAll(mobsManager);
        AmbientSounds ambient = new AmbientSounds();
        CaveInsPlayerListener caveIns = new CaveInsPlayerListener();
        Bukkit.getPluginManager().registerEvents(caveIns, this);

        dynamics.subscribe(ambient);
        dynamics.subscribe(mobsManager);

        cfg.register(caveIns);
        cfg.register(new CaveGenerator());
        cfg.register(ambient);

        new DangerousCavesOld().onEnable();
    }

    public MobsManager getMobs() {
        return mobsManager;
    }
}
