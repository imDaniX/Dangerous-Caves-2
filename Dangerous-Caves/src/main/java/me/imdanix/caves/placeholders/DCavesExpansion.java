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
import me.imdanix.caves.caverns.DepthHypoxia;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.ToDoubleFunction;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
public class DCavesExpansion extends PlaceholderExpansion {

    private final DepthHypoxia hypoxia;

    private ToDoubleFunction<Player> currentHypoxia;

    public DCavesExpansion(DepthHypoxia hypoxia) {
        this.hypoxia = hypoxia;
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
    public String onPlaceholderRequest(Player player, String identifier){
        if(player == null) return "";
        switch(identifier.toLowerCase()) {
            case "hypoxia_chance":
                return Math.floor(currentHypoxia.applyAsDouble(player)*10000)/100 + "%";
            default:
                return null;
        }
    }

    private class HypoxiaCacher extends BukkitRunnable implements ToDoubleFunction<Player> {
        private final Map<UUID, Double> cache;

        public HypoxiaCacher() {
            cache = new HashMap<>();
        }

        @Override
        public void run() {

        }

        @Override
        public double applyAsDouble(Player player) {
            return cache.computeIfAbsent(player.getUniqueId(), u -> hypoxia.getChance(player));
        }
    }
}