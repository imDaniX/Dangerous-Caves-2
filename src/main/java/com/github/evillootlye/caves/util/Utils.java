package com.github.evillootlye.caves.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.Collection;
import java.util.List;

public final class Utils {
    public static String clr(String s){
        return s == null ? "\u00A74Error" : ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> clr(List<String> list) {
        list.replaceAll(Utils::clr);
        return list;
    }

    public static void fillWorlds(List<String> worldsCfg, Collection<String> worlds) {
        if(worldsCfg.isEmpty()) {
            for(World world : Bukkit.getWorlds()) {
                if(world.getEnvironment() == World.Environment.NORMAL)
                    worlds.add(world.getName());
            }
        } else worlds.addAll(worldsCfg);
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
}
