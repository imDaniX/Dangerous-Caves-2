package com.github.evillootlye.caves.mobs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class TickableMob extends CustomMob {
    public TickableMob(EntityType base, String id) {
        super(base, id);
    }

    public abstract void tick(LivingEntity entity);
}
