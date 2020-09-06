/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.caves.mobs;

import me.imdanix.caves.compatibility.Compatibility;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Locale;

public abstract class AbstractMob implements CustomMob {
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

    public abstract void configure(ConfigurationSection cfg);

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
    public String getPath() {
        return "mobs." + customType;
    }
}
