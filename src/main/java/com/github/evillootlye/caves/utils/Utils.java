package com.github.evillootlye.caves.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String clr(String s){
        return s == null ? clr("&4Error") : ChatColor.translateAlternateColorCodes('&', s);
    }
    public static List<String> clr(List<String> list) {
        List<String> clred = new ArrayList<>();
        list.forEach(s -> clred.add(clr(s)));
        return clred;
    }
}
