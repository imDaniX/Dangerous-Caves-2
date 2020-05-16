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

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class TickingMob extends CustomMob {
    public TickingMob(EntityType base, String id) {
        super(base, id);
    }
    public TickingMob(EntityType base, String id, int weight) {
        super(base, id, weight);
    }

    @Override
    public LivingEntity spawn(Location loc) {
        LivingEntity entity = super.spawn(loc);
        MobsManager.handle(entity, this);
        return entity;
    }

    public abstract void tick(LivingEntity entity);
}
