package me.imdanix.caves.mobs;

import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.configuration.Configurable;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Locale;

@Configurable.Before("mobs")
public abstract class AbstractMob implements CustomMob, Configurable {
    private final EntityType type;
    private final String customType;
    private int weight;

    private final int defWeight;

    public AbstractMob(EntityType base, String id, int weight) {
        this.type = base.isAlive() ? base : EntityType.ZOMBIE;
        this.customType = id.toLowerCase(Locale.ENGLISH);
        this.defWeight = weight;
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public String getCustomType() {
        return customType;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", defWeight);
        configure(cfg);
    }

    protected abstract void configure(ConfigurationSection cfg);

    @Override
    public boolean isThis(Entity entity) {
        return entity instanceof LivingEntity && customType.equals(Compatibility.getTag((LivingEntity) entity));
    }

    @Override
    public LivingEntity spawn(Location loc) {
        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
        // TODO: health and name
        setup(entity);
        return entity;
    }

    @Override
    public String getConfigPath() {
        return "mobs." + customType;
    }
}
