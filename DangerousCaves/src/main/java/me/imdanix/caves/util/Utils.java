package me.imdanix.caves.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public final class Utils {
    private final static Pattern FLOAT = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static String clr(String s){
        return s == null ? null : ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> clr(List<String> list) {
        list.replaceAll(Utils::clr);
        return list;
    }

    public static void fillWorlds(List<String> worldsStr, Collection<String> into) {
        into.clear();
        if (worldsStr.isEmpty()) {
            for (World world : Bukkit.getWorlds()) {
                if (world.getEnvironment() == World.Environment.NORMAL)
                    into.add(world.getName());
            }
        } else into.addAll(worldsStr);
    }

    public static void setMaxHealth(LivingEntity entity, double health) {
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        entity.setHealth(health);
    }

    public static String capitalize(String str) {
        StringBuilder builder = new StringBuilder(str.length() + 1);
        String[] split = str.split(" ");
        for (String word : split) {
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(' ');
        }
        return builder.substring(0, builder.length() - 1);
    }

    public static <T extends Enum<T>> Set<T> getEnumSet(Class<T> clazz, Collection<String> enumStrColl) {
        Set<T> enums = new HashSet<>();
        for (String enumStr : enumStrColl) {
            T t = getEnum(clazz, enumStr.toUpperCase(Locale.ROOT));
            if (t != null) enums.add(t);
        }
        return enums.isEmpty() ? Collections.emptySet() : EnumSet.copyOf(enums);
    }

    public static <T extends Enum<T>> T getEnum(Class<T> clazz, String name) {
        return getEnum(clazz, name, null);
    }

    public static <T extends Enum<T>> T getEnum(Class<T> clazz, String name, T def) {
        try {
            return Enum.valueOf(clazz, name);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public static <T> void runIteratingTask(Plugin plugin, Collection<T> col, Consumer<T> action, long timeout) {
        Iterator<T> iterator = col.iterator();
        plugin.getServer().getScheduler().runTaskTimer(plugin, (task) -> {
            if (iterator.hasNext()) {
                action.accept(iterator.next());
            } else {
                task.cancel();
            }
        }, timeout, timeout);
    }

    public static double getDouble(String str, double def) {
        if (!FLOAT.matcher(str).matches()) return def;
        return Double.parseDouble(str);
    }
}
