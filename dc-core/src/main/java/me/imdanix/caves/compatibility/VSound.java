package me.imdanix.caves.compatibility;

import me.imdanix.caves.util.Utils;
import org.bukkit.Sound;

public enum VSound {
    ENTITY_ZOMBIE_BREAK_WOODEN_DOOR("ENTITY_ZOMBIE_BREAK_WOODEN_DOOR", "ENTITY_ZOMBIE_BREAK_DOOR_WOOD"),
    BLOCK_ENDER_CHEST_CLOSE("BLOCK_ENDER_CHEST_CLOSE", "BLOCK_ENDERCHEST_CLOSE");

    private final Sound sound;

    VSound(String... soundsStr) {
        for(String soundStr : soundsStr) {
            Sound sound = Utils.getEnum(Sound.class, soundStr);
            if(sound == null) continue;
            this.sound = sound;
            return;
        }
        this.sound = null;
    }

    public Sound get() {
        return sound;
    }
}
