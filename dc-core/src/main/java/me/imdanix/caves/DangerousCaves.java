package me.imdanix.caves;

import me.imdanix.caves.caverns.AmbientSounds;
import me.imdanix.caves.caverns.CaveIns;
import me.imdanix.caves.caverns.CavesAging;
import me.imdanix.caves.caverns.DepthTemperature;
import me.imdanix.caves.commands.Commander;
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.generator.CaveGenerator;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.ticks.Dynamics;
import me.imdanix.caves.util.PlayerAttackedEvent;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DangerousCaves extends JavaPlugin implements Listener {
    public static Plugin PLUGIN;

    private MobsManager mobsManager;
    private Dynamics dynamics;
    private Configuration cfg;
    private CaveGenerator generator;

    @Override
    public void onEnable() {
        DangerousCaves.PLUGIN = this;

        dynamics = new Dynamics(this);
        cfg = new Configuration(this, "config"); cfg.create(true);
        mobsManager = new MobsManager(this); DefaultMobs.registerAll(mobsManager);
        generator = new CaveGenerator(cfg); DefaultStructures.registerAll(generator);

        AmbientSounds ambient = new AmbientSounds();
        CaveIns caveIns = new CaveIns();
        CavesAging cavesAging = new CavesAging();
        DepthTemperature temperature = new DepthTemperature();

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(mobsManager, this);
        Bukkit.getPluginManager().registerEvents(caveIns, this);

        dynamics.subscribe(mobsManager);
        dynamics.subscribe(ambient);
        dynamics.subscribe(cavesAging);
        dynamics.subscribe(temperature);

        cfg.register(mobsManager);
        cfg.register(ambient);
        cfg.register(cavesAging);
        cfg.register(caveIns);
        cfg.register(temperature);
        if(cfg.getYml().getBoolean("generator.wait-other", false)) {
            Bukkit.getScheduler().runTaskLater(this, () -> cfg.register(generator), 1);
        } else cfg.register(generator);

        PluginCommand cmd = getCommand("dangerouscaves");
        if(cmd != null) cmd.setExecutor(new Commander(mobsManager, cfg, dynamics));

        cfg.checkVersion();

        new MetricsLite(this, 6824);
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        if(event.getEntityType() == EntityType.PLAYER && event.getDamager() instanceof LivingEntity) {
            PlayerAttackedEvent pEvent = new PlayerAttackedEvent((Player) event.getEntity(), (LivingEntity)event.getDamager(), event.getDamage());
            Bukkit.getPluginManager().callEvent(pEvent);
            event.setDamage(pEvent.getDamage());
            event.setCancelled(pEvent.isCancelled());
        }
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
