package me.imdanix.caves;

import me.imdanix.caves.caverns.AmbientSounds;
import me.imdanix.caves.caverns.CaveIns;
import me.imdanix.caves.caverns.CavesAging;
import me.imdanix.caves.caverns.DepthHypoxia;
import me.imdanix.caves.commands.Commander;
import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.generator.CaveGenerator;
import me.imdanix.caves.generator.defaults.DefaultStructures;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.mobs.defaults.DefaultMobs;
import me.imdanix.caves.placeholders.DCExpansion;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.ticks.Dynamics;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DangerousCaves extends JavaPlugin {
    private MobsManager mobsManager;
    private Dynamics dynamics;
    private Configuration cfg;
    private CaveGenerator generator;

    @Override
    public void onLoad() {
        if (getDescription().getVersion().contains("SNAPSHOT")) {
            getLogger().info("Thank you for using dev-build of the plugin! But please note that this version may " +
                    "contain bugs. If you found some - report it to https://github.com/imDaniX/Dangerous-Сaves-2/issues");
        }
        Regions.INSTANCE.onLoad();
        Compatibility.init(this);
    }

    @Override
    public void onEnable() {
        Regions.INSTANCE.onEnable();

        dynamics = new Dynamics(this);
        cfg = new Configuration(this, "config", getDescription().getVersion().split(";")[1]); cfg.create(true);
        mobsManager = new MobsManager(this, cfg, dynamics);
        generator = new CaveGenerator(cfg);
        DefaultMobs.registerAll(mobsManager);
        DefaultStructures.registerAll(generator);

        AmbientSounds ambient = new AmbientSounds();
        CaveIns caveIns = new CaveIns();
        CavesAging cavesAging = new CavesAging(this);
        DepthHypoxia hypoxia = new DepthHypoxia(this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            DCExpansion expansion = new DCExpansion(cfg);
            expansion.register(hypoxia.getPlaceholder());
            expansion.register();
        }

        Bukkit.getPluginManager().registerEvents(mobsManager, this);
        Bukkit.getPluginManager().registerEvents(caveIns, this);

        dynamics.register(mobsManager);
        dynamics.register(ambient);
        dynamics.register(cavesAging);
        dynamics.register(hypoxia);

        cfg.register(mobsManager);
        cfg.register(ambient);
        cfg.register(cavesAging);
        cfg.register(caveIns);
        cfg.register(hypoxia);
        if (cfg.getYml().getBoolean("generator.wait-other", false)) {
            Bukkit.getScheduler().runTaskLater(this, () -> cfg.register(generator), 1);
        } else cfg.register(generator);

        Objects.requireNonNull(getCommand("dangerouscaves")).setExecutor(new Commander(this));

        cfg.checkVersion(true);

        new MetricsLite(this, 6824);

        Bukkit.getScheduler().runTask(this, () -> {
            Regions.INSTANCE.onDone();
            cfg.register(Regions.INSTANCE);
        });
    }

    public MobsManager getMobs() {
        return mobsManager;
    }

    public Dynamics getDynamics() {
        return dynamics;
    }

    public Configuration getConfiguration() {
        return cfg;
    }

    public CaveGenerator getGenerator() {
        return generator;
    }
}
