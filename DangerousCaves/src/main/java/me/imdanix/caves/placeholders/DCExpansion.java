package me.imdanix.caves.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.util.Manager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DCExpansion extends PlaceholderExpansion implements Manager<Placeholder> {
    private final Map<String, Placeholder> placeholders;
    private final Configuration config;

    public DCExpansion(Configuration config) {
        this.placeholders = new HashMap<>();
        this.config = config;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return "imDaniX";
    }

    @Override
    public String getIdentifier(){
        return "dangerouscaves";
    }

    @Override
    public String getVersion(){
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        return placeholders.getOrDefault(identifier.toLowerCase(Locale.ROOT), Placeholder.EMPTY).getValue(player);
    }

    @Override
    public boolean register(Placeholder placeholder) {
        if (!placeholders.containsKey(placeholder.getName())) {
            placeholders.put(placeholder.getName(), placeholder);
            if (placeholder instanceof Configurable configurable)
                config.register(configurable);
            return true;
        }
        return false;
    }
}