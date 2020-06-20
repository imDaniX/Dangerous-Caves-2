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

import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.compatibility.VMaterial;
import me.imdanix.caves.mobs.AbstractMob;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AlphaSpider extends AbstractMob implements Listener {
    private static final PotionEffect POISON = new PotionEffect(PotionEffectType.POISON, 75, 1);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false, true);

    private String name;
    private double health;
    private double cobwebChance;
    private double minionChance;

    public AlphaSpider() {
        super(EntityType.SPIDER, "alpha-spider", 9);
    }

    @Override
    public void configure(ConfigurationSection cfg) {
        name = Utils.clr(cfg.getString("name", "&4Alpha Spider"));
        health = cfg.getDouble("health", 16);
        cobwebChance = cfg.getDouble("cobweb-chance", 14.29) / 100;
        minionChance = cfg.getDouble("minion-chance", 6.67) / 100;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        entity.addPotionEffect(REGENERATION);
        Utils.setMaxHealth(entity, health);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!isThis(event.getDamager())) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        Entity damager = event.getDamager();
        if (Rnd.nextBoolean()) {
            if (minionChance > 0 && Rnd.chance(minionChance))
                damager.getWorld().spawnEntity(damager.getLocation(), EntityType.CAVE_SPIDER);

            if (cobwebChance > 0) {
                Location loc = event.getEntity().getLocation();
                loc.getBlock().setType(VMaterial.COBWEB.get());
                entity.getEyeLocation().getBlock().setType(VMaterial.COBWEB.get());

                Locations.loop(3, loc, l -> {
                    if (Compatibility.isAir(l.getBlock().getType()) && Rnd.chance(cobwebChance) &&
                            Regions.INSTANCE.check(CheckType.ENTITY, l)) {
                        l.getBlock().setType(VMaterial.COBWEB.get());
                    }}
                );
            }
        } else entity.addPotionEffect(POISON);
    }
}
