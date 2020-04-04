package me.imdanix.caves.mobs;

import me.imdanix.caves.compatibility.Compatibility;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class CustomMob {
    private final EntityType type;
    private final String id;

    public CustomMob(EntityType base, String id) {
        this.type = base.isAlive() ? base : EntityType.ZOMBIE;
        this.id = id;
    }

    public final boolean isThis(Entity entity) {
        return entity instanceof LivingEntity && id.equals(Compatibility.getTag((LivingEntity) entity));
    }

    public final EntityType getType() {
        return type;
    }

    public final String getCustomType() {
        return id;
    }

    public LivingEntity spawn(Location loc) {
        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
        Compatibility.setTag(entity, id);
        setup(entity);
        return entity;
    }

    public boolean canSpawn(EntityType type, Location loc) {
        return true;
    }

    public abstract void setup(LivingEntity entity);

    public abstract int getWeight();
}
