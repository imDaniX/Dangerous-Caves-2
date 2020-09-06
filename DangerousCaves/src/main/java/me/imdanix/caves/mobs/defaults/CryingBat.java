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

package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.mobs.TickingMob;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class CryingBat extends TickingMob {
    private String name;
    private double cryChance;
    private double deathChance;

    public CryingBat() {
        super(EntityType.BAT, "crying-bat", 9);
    }

    @Override
    public void configure(ConfigurationSection cfg) {
        name = Utils.clr(cfg.getString("name", "&4Crying Bat"));
        cryChance = cfg.getDouble("cry-chance", 3.33) / 100;
        deathChance = cfg.getDouble("death-chance", 20) / 100;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
    }

    @Override
    public void tick(LivingEntity entity) {
        if(cryChance > 0 && Rnd.chance(cryChance)) {
            Locations.playSound(entity.getLocation(), Sound.ENTITY_WOLF_WHINE, 1, (float) (1.4 + Rnd.nextDouble(0.6)));
            if(deathChance > 0 && Rnd.chance(deathChance))
                entity.damage(1000);
        }
    }
}
