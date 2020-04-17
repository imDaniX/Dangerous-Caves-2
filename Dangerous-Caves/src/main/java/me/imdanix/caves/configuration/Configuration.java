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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private final Plugin plugin;
    private final Map<Configurable, String> configurables;
    private final File file;
    private final String version;
    private YamlConfiguration yml;

    public Configuration(Plugin plugin, String name) {
        this.plugin = plugin;
        configurables = new HashMap<>();
        file = new File(plugin.getDataFolder(), name + ".yml");
        String versionPlg = plugin.getDescription().getVersion();
        version = versionPlg.substring(versionPlg.lastIndexOf("-") + 1);
    }

    public void create(boolean resource) {
        file.getParentFile().mkdirs();
        if(!file.exists()) {
            if(resource)
                plugin.saveResource(file.getName(), false);
            else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        yml = YamlConfiguration.loadConfiguration(file);
    }

    public void register(Configurable conf) {
        if(!configurables.containsKey(conf)) {
            String path = conf.getPath();
            configurables.put(conf, path);
            reload(conf, path);
        }
    }

    public void reloadYml() {
        yml = YamlConfiguration.loadConfiguration(file);
        configurables.entrySet().forEach(this::reload);
    }

    private void reload(Map.Entry<Configurable, String> entry) {
        reload(entry.getKey(), entry.getValue());
    }

    public void reload(Configurable conf, String path) {
        conf.reload(path.isEmpty() ? yml : section(yml, path));
    }

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
