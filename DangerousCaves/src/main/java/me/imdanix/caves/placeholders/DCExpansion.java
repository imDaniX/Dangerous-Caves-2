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

package me.imdanix.caves.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.imdanix.caves.Manager;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DCExpansion extends PlaceholderExpansion implements Manager<Placeholder> {
    private final Map<String, Placeholder> placeholders;
    private final Configuration cfg;

    public DCExpansion(Configuration cfg) {
        this.placeholders = new HashMap<>();
        this.cfg = cfg;
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
        return placeholders.getOrDefault(identifier.toLowerCase(Locale.ENGLISH), Placeholder.EMPTY).getValue(player);
    }

    @Override
    public boolean register(Placeholder placeholder) {
        if (!placeholders.containsKey(placeholder.getName())) {
            placeholders.put(placeholder.getName(), placeholder);
            if (placeholder instanceof Configurable) {
                cfg.register((Configurable)placeholder);
            }
            return true;
        }
        return false;
    }
}