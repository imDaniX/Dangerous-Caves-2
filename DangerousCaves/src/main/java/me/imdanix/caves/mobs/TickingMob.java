package me.imdanix.caves.mobs;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class TickingMob extends AbstractMob implements CustomMob.Ticking {

    public TickingMob(EntityType type, String id, int weight) {
        super(type, id, weight, null);
    }

    public TickingMob(EntityType type, String id, int weight, Double health) {
        super(type, id, weight, health);
    }

    public TickingMob(EntityType type, String id, int weight, Double health, String name) {
        super(type, id, weight, health, name);
    }

    @Override
    public LivingEntity spawn(Location loc) {
        LivingEntity entity = super.spawn(loc);
        MobsManager.handle(entity, this);
        return entity;
    }
}
