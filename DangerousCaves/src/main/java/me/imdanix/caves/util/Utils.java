/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.caves.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class Utils {
    private final static Pattern FLOAT = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static int square(int i) {
        return i*i;
    }

    public static String clr(String s){
        return s == null ? "\u00A74Error" : ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> clr(List<String> list) {
        list.replaceAll(Utils::clr);
        return list;
    }

    public static void fillWorlds(List<String> worldsCfg, Collection<String> worlds) {
        if (worldsCfg.isEmpty()) {
            for (World world : Bukkit.getWorlds()) {
                if (world.getEnvironment() == World.Environment.NORMAL)
                    worlds.add(world.getName());
            }
        } else worlds.addAll(worldsCfg);
    }

    public static void setMaxHealth(LivingEntity entity, double health) {
        if (health <= 0) return;
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        entity.setHealth(health);
    }

    public static <T extends Enum<T>> Set<T> getEnumSet(Class<T> clazz, Collection<String> enumStrColl) {
        Set<T> enums = new HashSet<>();
        for (String enumStr : enumStrColl) {
            T t = getEnum(clazz, enumStr.toUpperCase(Locale.ENGLISH));
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

    public static double getDouble(String str, double def) {
        if (!FLOAT.matcher(str).matches()) return def;
        return Double.parseDouble(str);
    }
}
