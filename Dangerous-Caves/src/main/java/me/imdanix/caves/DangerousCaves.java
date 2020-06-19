/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.caves;

import me.imdanix.caves.caverns.AmbientSounds;
import me.imdanix.caves.caverns.CaveIns;
import me.imdanix.caves.caverns.CavesAging;
import me.imdanix.caves.caverns.DepthHypoxia;
import me.imdanix.caves.commands.Commander;
import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.generator.CaveGenerator;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.ticks.Dynamics;
import me.imdanix.caves.util.PlayerAttackedEvent;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DangerousCaves extends JavaPlugin implements Listener {
    private MobsManager mobsManager;
    private Dynamics dynamics;
    private Configuration cfg;
    private CaveGenerator generator;

    @Override
    public void onLoad() {
        Regions.INSTANCE.onLoad();
    }

    @Override
    public void onEnable() {
        if(getDescription().getVersion().contains("SNAPSHOT")) {
            getLogger().info("Thank you for using dev-build of the plugin! But please note that this version may " +
                    "contain bugs. If you found some - report it to https://github.com/imDaniX/dangerous-caves/issues");
        }
        Regions.INSTANCE.onEnable();
        Compatibility.init(this);

        dynamics = new Dynamics(this);
        cfg = new Configuration(this, "config", getDescription().getVersion().split(";")[1]); cfg.create(true);
        mobsManager = new MobsManager(this, cfg, dynamics); DefaultMobs.registerAll(mobsManager);
        generator = new CaveGenerator(cfg); DefaultStructures.registerAll(generator);

        AmbientSounds ambient = new AmbientSounds();
        CaveIns caveIns = new CaveIns();
        CavesAging cavesAging = new CavesAging(this);
        DepthHypoxia temperature = new DepthHypoxia();

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(mobsManager, this);
        Bukkit.getPluginManager().registerEvents(caveIns, this);

        dynamics.register(mobsManager);
        dynamics.register(ambient);
        dynamics.register(cavesAging);
        dynamics.register(temperature);

        cfg.register(Regions.INSTANCE);
        cfg.register(mobsManager);
        cfg.register(ambient);
        cfg.register(cavesAging);
        cfg.register(caveIns);
        cfg.register(temperature);
        if(cfg.getYml().getBoolean("generator.wait-other", false)) {
            Bukkit.getScheduler().runTaskLater(this, () -> cfg.register(generator), 1);
        } else cfg.register(generator);

        Objects.requireNonNull(getCommand("dangerouscaves")).setExecutor(new Commander(this));

        cfg.checkVersion();

        new MetricsLite(this, 6824);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        if(event.getEntityType() == EntityType.PLAYER && event.getDamager() instanceof LivingEntity) {
            PlayerAttackedEvent pEvent = new PlayerAttackedEvent((Player) event.getEntity(), (LivingEntity)event.getDamager(), event.getDamage());
            Bukkit.getPluginManager().callEvent(pEvent);
            event.setDamage(pEvent.getDamage());
            if(pEvent.isCancelled()) event.setCancelled(true);
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
