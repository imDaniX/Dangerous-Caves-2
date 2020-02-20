package com.github.evillootlye.caves.configuration;

import com.github.evillootlye.caves.DangerousCaves;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private final Map<Configurable, String> configurables;
    private final File file;
    private YamlConfiguration yml;

    public Configuration(String name) {
        configurables = new HashMap<>();
        file = new File(DangerousCaves.INSTANCE.getDataFolder(), name + ".yml");
    }

    public void registerConfigurable(Configurable conf) {
        configurables.put(conf, Configurable.getPath(conf));
    }

    public void create(boolean resource) {
        file.getParentFile().mkdirs();
        if(!file.exists()) {
            if(resource)
                DangerousCaves.INSTANCE.saveResource(file.getName(), false);
            else
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void reload() {
        try {
            yml = YamlConfiguration.loadConfiguration(file);
            for(Map.Entry<Configurable, String> entry : configurables.entrySet()) {
                String path = entry.getValue();
                if(path.isEmpty())
                    entry.getKey().reload(yml);
                else
                    entry.getKey().reload(yml.isConfigurationSection(path) ?
                        yml.getConfigurationSection(path) : yml.createSection(path));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getYml() {
        return yml;
    }
}
