package me.imdanix.caves.configuration;

import org.bukkit.configuration.ConfigurationSection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Configurable {
    /**
     * Reload object on config reload
     * @param cfg Related configuration section
     */
    void reload(ConfigurationSection cfg);

    /**
     * Get path for this object in configuration
     * @return Object's configuration section path
     */
    String getConfigPath();

    /**
     * Get name of configuration for this object
     * @return Name of required configuration
     */
    default String getConfigName() {
        return "config";
    }

    @Inherited
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Before {
        String value();
    }
}
