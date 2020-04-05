package me.imdanix.caves.configuration;

import org.bukkit.configuration.ConfigurationSection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Configurable {
    void reload(ConfigurationSection cfg);

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Path {
        String value();
    }

    static String getPath(Object obj) {
        Class<?> clazz = obj.getClass();
        return clazz.isAnnotationPresent(Path.class) ? clazz.getAnnotation(Path.class).value() : "";
    }
}
