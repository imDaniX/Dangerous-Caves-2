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

package me.imdanix.caves.configuration;

import me.imdanix.caves.Manager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Configuration implements Manager<Configurable> {
    private final Plugin plugin;
    private final Set<Configurable> configurables;
    private final File file;
    private final String version;
    private YamlConfiguration yml;

    public Configuration(Plugin plugin, String name) {
        this.plugin = plugin;
        configurables = new HashSet<>();
        file = new File(plugin.getDataFolder(), name + ".yml");
        version = plugin.getDescription().getVersion().split(";")[1];
    }

    /**
     * Create a new file
     * @param resource Copy from jar?
     */
    public void create(boolean resource) {
        file.getParentFile().mkdirs();
        if(!file.exists()) {
            if(resource) {
                plugin.saveResource(file.getName(), false);
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        yml = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Register and reload new configurable object for this configuration
     * @param conf Object to register
     */
    @Override
    public boolean register(Configurable conf) {
        if(!configurables.contains(conf)) {
            configurables.add(conf);
            reload(conf);
            return true;
        }
        return false;
    }

    /**
     * Reload configuration and all its configurable objects
     */
    public void reloadYml() {
        yml = YamlConfiguration.loadConfiguration(file);
        configurables.forEach(this::reload);
    }

    /**
     * Reload configurable object
     * @param conf Object to reload
     */
    public void reload(Configurable conf) {
        conf.reload(conf.getPath().isEmpty() ? yml : section(yml, conf.getPath()));
    }

    /**
     * Get origin of this configuration
     * @return Origin YAML of this configuration
     */
    public YamlConfiguration getYml() {
        return yml;
    }

    public void checkVersion() {
        String oldVersion = yml.getString("version", "0");
        if(!version.equals(oldVersion)) {
            plugin.getLogger().warning("Seems like your config is outdated (current " + version + ", your " + oldVersion + ")");
            plugin.getLogger().warning("Please check latest changes, and if everything is good, change your version in config.yml to " + version);
        }
    }

    public static ConfigurationSection section(ConfigurationSection cfg, String path) {
        return cfg.isConfigurationSection(path) ?
                cfg.getConfigurationSection(path) : cfg.createSection(path);
    }
}
