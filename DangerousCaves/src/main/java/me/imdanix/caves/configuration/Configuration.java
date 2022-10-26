package me.imdanix.caves.configuration;

import me.imdanix.caves.util.Manager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class Configuration implements Manager<Configurable> {
    private final Plugin plugin;
    private final Set<Configurable> configurables;
    private final String name;
    private final File file;
    private final String version;
    private YamlConfiguration yml;

    public Configuration(Plugin plugin, String name, String version) {
        this.plugin = plugin;
        this.name = name;
        configurables = new TreeSet<>((incoming, checked) -> {
            if (incoming == checked) return 0;
            Configurable.Before before = incoming.getClass().getAnnotation(Configurable.Before.class);
            return before != null && before.value().equals(checked.getConfigPath())
                    ? -1
                    : Integer.compare(incoming.hashCode(), checked.hashCode());
        });
        file = new File(plugin.getDataFolder(), name + ".yml");
        this.version = version;
    }

    /**
     * Create a new file
     * @param resource Copy from jar?
     */
    public void create(boolean resource) {
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            if (resource) {
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
        if (conf.getConfigName().equals(name) && !configurables.contains(conf)) {
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
        conf.reload(conf.getConfigPath().isEmpty() ? yml : section(yml, conf.getConfigPath()));
    }

    /**
     * Get origin of this configuration
     * @return Origin YAML of this configuration
     */
    public YamlConfiguration getYml() {
        return yml;
    }

    /**
     * Get name of this configuration
     * @return Name of configuration
     */
    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    /**
     * Compare version of configuration with version in the file
     * @param message Throw default warning if versions don't match?
     * @return Are versions equal
     */
    public boolean checkVersion(boolean message) {
        if (yml == null) {
            throw new IllegalStateException("Configuration file is not created yet.");
        }
        String oldVersion = yml.getString("version", "0");
        if (version.equals(oldVersion)) return true;
        if (message) {
            plugin.getLogger().warning("Seems like your config is outdated (current " + version + ", your " + oldVersion + ")");
            plugin.getLogger().warning("Please check latest changes, and if everything is good, change your version in config.yml to " + version);
        }
        return false;
    }

    public static ConfigurationSection section(ConfigurationSection cfg, String path) {
        return cfg.isConfigurationSection(path) ?
                cfg.getConfigurationSection(path) : cfg.createSection(path);
    }
}
