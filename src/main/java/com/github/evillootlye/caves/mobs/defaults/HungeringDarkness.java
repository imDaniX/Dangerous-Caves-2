package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.CustomMob;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

public class HungeringDarkness extends CustomMob.TickableMob implements Listener, Configurable {
    private int weight;

    public HungeringDarkness() {
        super(EntityType.HUSK, "hungering-darkness");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("weight");
    }

    @Override
    public void setup(LivingEntity entity) {

    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void tick(Entity entity) {

    }
}
