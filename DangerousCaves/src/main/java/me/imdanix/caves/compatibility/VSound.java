package me.imdanix.caves.compatibility;

import org.bukkit.Sound;

public enum VSound {
    ENTITY_ZOMBIE_BREAK_WOODEN_DOOR("ENTITY_ZOMBIE_BREAK_WOODEN_DOOR", "ENTITY_ZOMBIE_BREAK_DOOR_WOOD"),
    BLOCK_ENDER_CHEST_CLOSE("BLOCK_ENDER_CHEST_CLOSE", "BLOCK_ENDERCHEST_CLOSE"),
    ENTITY_PHANTOM_SWOOP("ENTITY_PHANTOM_SWOOP", "ENTITY_SILVERFISH_DEATH");

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
