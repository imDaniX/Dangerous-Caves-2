package com.github.evillootlye.caves.mobs;

import com.github.evillootlye.caves.DangerousCaves;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

public abstract class CustomMob {
    private static final NamespacedKey KEY = new NamespacedKey(DangerousCaves.INSTANCE, "dangerouscaves-mob-type");
    private final EntityType type;
    private final String id;

    public CustomMob(EntityType base, String id) {
        this.type = base.isAlive() ? base : EntityType.ZOMBIE;
        this.id = id;
    }

    public final boolean isThis(Entity entity) {
        return id.equals(CustomMob.getCustomType(entity));
    }

    public boolean canSpawn(EntityType type, Location loc) {
        return true;
    }

    public final void spawn(Location loc) {
        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
        entity.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, id);
        setup(entity);
    }

    public abstract void setup(LivingEntity entity);

    public String getId() {
        return id;
    }

    public abstract int getWeight();

    public static boolean isCustomMob(Entity entity) {
        return entity.getPersistentDataContainer().has(KEY, PersistentDataType.STRING);
    }

    public static String getCustomType(Entity entity) {
        return isCustomMob(entity) ? entity.getPersistentDataContainer().get(KEY, PersistentDataType.STRING) : null;
    }

    public static abstract class TickableMob extends CustomMob {
        public TickableMob(EntityType base, String id) {
            super(base, id);
        }

        public abstract void tick(Entity entity);
    }
}
