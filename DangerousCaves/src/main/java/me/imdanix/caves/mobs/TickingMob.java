package me.imdanix.caves.mobs;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class TickingMob extends AbstractMob implements CustomMob.Ticking {
    public TickingMob(EntityType base, String id, int weight) {
        super(base, id, weight);
    }

    @Override
    public LivingEntity spawn(Location loc) {
        LivingEntity entity = super.spawn(loc);
        MobsManager.handle(entity, this);
        return entity;
    }
}
