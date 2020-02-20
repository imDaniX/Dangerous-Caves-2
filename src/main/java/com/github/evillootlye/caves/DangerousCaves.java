package com.github.evillootlye.caves;

import com.github.evillootlye.caves.configuration.Configuration;
import com.github.evillootlye.caves.mobs.MobsManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DangerousCaves extends JavaPlugin {
    public static DangerousCaves INSTANCE;

    @Override
    @SuppressWarnings("deprecation")
    public void onEnable() {
        DangerousCaves.INSTANCE = this;
        Dynamics dynamics = new Dynamics();
        Configuration cfg = new Configuration("config"); cfg.create(true);
        MobsManager mobsManager = new MobsManager(cfg);
        dynamics.registerTickable(mobsManager);

        new DangerousCavesOld();
    }
}
