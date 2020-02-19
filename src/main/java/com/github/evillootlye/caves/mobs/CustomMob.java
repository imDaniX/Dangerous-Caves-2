package com.github.evillootlye.caves.mobs;

import com.github.evillootlye.caves.DangerousCaves;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataType;

public abstract class CustomMob {
    private static final NamespacedKey KEY = new NamespacedKey(DangerousCaves.INSTANCE, "dangerouscaves-mob-type");
    private final EntityType type;
    private final String id;

    public CustomMob(EntityType base, String id) {
        this.type = base.isAlive() ? base : EntityType.ZOMBIE;
        this.id = id;
    }

    public boolean isThis(Entity entity) {
        return CustomMob.isCustomMob(entity) &&
                entity.getPersistentDataContainer().get(KEY, PersistentDataType.STRING).equals(id);
    }

    public void spawn(Location loc) {
        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
        entity.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, id);
        build(entity);
    }

    public abstract void build(LivingEntity entity);

    public static boolean isCustomMob(Entity entity) {
        if(!(entity instanceof LivingEntity)) return false;
        return entity.getPersistentDataContainer().has(KEY, PersistentDataType.STRING);
    }
}
