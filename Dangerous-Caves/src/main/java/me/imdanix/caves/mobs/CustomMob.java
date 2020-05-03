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
import me.imdanix.caves.configuration.Configurable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class CustomMob implements Configurable {
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

    public boolean canSpawn(Location loc) {
        return true;
    }

    public abstract void setup(LivingEntity entity);

    @Override
    public String getPath() {
        return "mobs." + id;
    }

    public abstract int getWeight();
}
