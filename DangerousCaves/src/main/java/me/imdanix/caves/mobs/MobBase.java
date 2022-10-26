package me.imdanix.caves.mobs;

import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.util.TagHelper;
import me.imdanix.caves.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Locale;

@Configurable.Before("mobs")
public abstract class MobBase implements CustomMob, Configurable {
    private final EntityType type;
    private final String customType;
    private final String scoreboardTag;

    private final int defWeight;
    private int weight;

    private final String defName;
    protected String name;

    private final Double defHealth;
    protected Double health;

    public MobBase(EntityType type, String id, int weight) {
        this(type, id, weight, null);
    }

    public MobBase(EntityType type, String id, int weight, Double health) {
        this(type, id, weight, health, ChatColor.DARK_RED + idToName(id));
    }

    public MobBase(EntityType type, String id, int weight, Double health, String name) {
        this.type = type;
        this.customType = id.toLowerCase(Locale.ROOT);
        this.scoreboardTag = TagHelper.mobScoreboardTag(customType);
        this.defWeight = weight;
        this.defName = name;
        this.defHealth = health;
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
        String nameCfg = cfg.getString("name", defName);
        name = nameCfg == null || nameCfg.isEmpty() ? null : Utils.clr(cfg.getString("name", defName));
        if (cfg.isDouble("health")) {
            health = Math.max(cfg.getDouble("health"), 1);
        } else {
            health = defHealth;
        }
        configure(cfg);
    }

    protected abstract void configure(ConfigurationSection cfg);

    @Override
    public boolean isThis(Entity entity) {
        return entity instanceof LivingEntity && entity.getScoreboardTags().contains(scoreboardTag);
    }

    @Override
    public LivingEntity spawn(Location loc) {
        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
        entity.setCustomName(name);
        if (health != null) Utils.setMaxHealth(entity, health);
        prepare(entity);
        return entity;
    }

    @Override
    public String getConfigPath() {
        return "mobs." + customType;
    }

    private static String idToName(String id) {
        return Utils.capitalize(id.replace('-', ' '));
    }
}
