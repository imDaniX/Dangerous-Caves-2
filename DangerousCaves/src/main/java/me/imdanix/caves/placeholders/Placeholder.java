package me.imdanix.caves.placeholders;

import org.bukkit.entity.Player;

public interface Placeholder {
    Placeholder EMPTY = new Placeholder() {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getValue(Player player) {
            return null;
        }
    };

    String getName();

    String getValue(Player player);
}
