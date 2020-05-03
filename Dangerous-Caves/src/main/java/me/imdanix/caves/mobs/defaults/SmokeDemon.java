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
import me.imdanix.caves.util.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SmokeDemon extends TickingMob {
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 120, 1);
    private static final PotionEffect WITHER = new PotionEffect(PotionEffectType.WITHER, 120, 0);
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);

    private int weight;
    private String name;
    private double health;

    private double radius;

    public SmokeDemon() {
        super(EntityType.ZOMBIE, "smoke-demon");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", "&4Smoke Demon"));
        health = cfg.getDouble("health", 20);

        radius = cfg.getInt("harm-radius", 3);
    }

    @Override
    public boolean canSpawn(Location loc) {
        return loc.getBlock().getLightLevel() < 12;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        Utils.setMaxHealth(entity, health);

        entity.setCustomNameVisible(false);
        entity.addPotionEffect(INVISIBILITY);
        entity.setSilent(true);
    }

    @Override
    public void tick(LivingEntity entity) {
        if(entity.getLocation().getBlock().getLightLevel() >= 12) {
            entity.remove();
            return;
        }
        entity.getNearbyEntities(radius, radius, radius).forEach(SmokeDemon::harm);
        entity.getWorld().spawnParticle(Particle.CLOUD, entity.getLocation().add(0, 1, 0), 30, 1, 1, 1, 0f);
    }

    private static void harm(Entity entity) {
        if(entity instanceof LivingEntity && entity.getLocation().getBlock().getLightLevel() < 12) {
            LivingEntity living = (LivingEntity) entity;
            living.addPotionEffect(BLINDNESS);
            living.addPotionEffect(WITHER);
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
