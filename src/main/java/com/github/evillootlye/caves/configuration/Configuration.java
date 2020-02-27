package com.github.evillootlye.caves.configuration;

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
    private YamlConfiguration yml;

    public Configuration(Plugin plugin, String name) {
        this.plugin = plugin;
        configurables = new HashMap<>();
        file = new File(plugin.getDataFolder(), name + ".yml");
    }

    public void create(boolean resource) {
        file.getParentFile().mkdirs();
        if(!file.exists()) {
            if(resource)
                plugin.saveResource(file.getName(), false);
            else
                try {
                    file.createNewFile();
                    reloadYml();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void register(Configurable conf) {
        if(!configurables.containsKey(conf)) {
            String path = Configurable.getPath(conf);
            configurables.put(conf, path);
            reload(conf, path);
        }
    }

    public void reloadYml() {
        try {
            yml = YamlConfiguration.loadConfiguration(file);
            configurables.entrySet().forEach(this::reload);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static ConfigurationSection section(ConfigurationSection cfg, String path) {
        return cfg.isConfigurationSection(path) ?
                cfg.getConfigurationSection(path) : cfg.createSection(path);
    }
}
