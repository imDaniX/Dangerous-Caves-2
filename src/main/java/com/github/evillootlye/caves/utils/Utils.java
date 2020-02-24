package com.github.evillootlye.caves.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Utils {
    @SafeVarargs
    public static <T> T[] array(T... t) {
        return t;
    }

    public static String clr(String s){
        return s == null ? clr("&4Error") : ChatColor.translateAlternateColorCodes('&', s);
    }
    public static List<String> clr(List<String> list) {
        List<String> clred = new ArrayList<>();
        list.forEach(s -> clred.add(clr(s)));
        return clred;
    }

    public static void fillWorlds(List<String> worldsCfg, Set<String> worlds) {
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
