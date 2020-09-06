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

package me.imdanix.caves.compatibility;

import org.bukkit.Sound;

public enum VSound {
    ENTITY_ZOMBIE_BREAK_WOODEN_DOOR("ENTITY_ZOMBIE_BREAK_WOODEN_DOOR", "ENTITY_ZOMBIE_BREAK_DOOR_WOOD"),
    BLOCK_ENDER_CHEST_CLOSE("BLOCK_ENDER_CHEST_CLOSE", "BLOCK_ENDERCHEST_CLOSE");

    private final Sound sound;

    VSound(String... soundsStr) {
        Sound sound = null;
        for (String soundStr : soundsStr) {
            try {
                sound = Sound.valueOf(soundStr);
                break;
            } catch (IllegalArgumentException ignored) {}
        }
        this.sound = sound;
    }

    public Sound get() {
        return sound;
    }
}
