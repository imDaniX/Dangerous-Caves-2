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

    public void register(Configurable conf) {
        if(!configurables.containsKey(conf)) {
            String path = Configurable.getPath(conf);
            configurables.put(conf, path);
            reload(conf, path);
        }
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
        if(path.isEmpty())
            conf.reload(yml);
        else
            conf.reload(yml.isConfigurationSection(path) ?
                yml.getConfigurationSection(path) : yml.createSection(path));
    }

    public YamlConfiguration getYml() {
        return yml;
    }
}
