package com.github.evillootlye.caves;

import com.github.evillootlye.caves.mobs.MobsManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DangerousCaves extends JavaPlugin {
    public static DangerousCaves INSTANCE;

    @Override
    public void onEnable() {
        DangerousCaves.INSTANCE = this;
        Dynamics dynamics = new Dynamics();
        MobsManager mobsManager = new MobsManager();
        dynamics.registerTickable(mobsManager);
    }
}
