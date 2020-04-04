package me.imdanix.caves.mobs;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class TickableMob extends CustomMob {
    public TickableMob(EntityType base, String id) {
        super(base, id);
    }

    @Override
    public LivingEntity spawn(Location loc) {
        LivingEntity entity = super.spawn(loc);
        MobsManager.handle(entity, this);
        return entity;
    }

    public abstract void tick(LivingEntity entity);
}
