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
import me.imdanix.caves.util.PlayerAttackedEvent;
import me.imdanix.caves.util.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HungeringDarkness extends TickingMob implements Listener {
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3, false, false);
    private int weight;
    private String name;
    private double damage;
    private boolean remove;

    public HungeringDarkness() {
        super(EntityType.HUSK, "hungering-darkness");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", ""));
        damage = cfg.getDouble("damage", 200);
        remove = cfg.getBoolean("remove-on-light", true);
    }

    @Override
    public boolean canSpawn(Location location) {
        return location.getBlock().getLightLevel() == 0;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        entity.setCustomNameVisible(false);
        entity.setSilent(true);
        entity.setInvulnerable(true);
        entity.setCanPickupItems(false);
        entity.setCustomNameVisible(false);
        entity.setCollidable(false);

        entity.addPotionEffect(INVISIBILITY);
        entity.addPotionEffect(SLOW);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if(!isThis(event.getEntity()) || event.getTarget() == null) return;
        if(event.getTarget().getLocation().getBlock().getLightLevel() > 0) {
            event.setCancelled(true);
            if(remove) event.getEntity().remove();
        }
    }

    @EventHandler
    public void onAttack(PlayerAttackedEvent event) {
        if(!isThis(event.getAttacker())) return;
        if(event.getPlayer().getLocation().getBlock().getLightLevel() > 0) {
            event.setCancelled(true);
            if(remove) event.getAttacker().remove();
        } else event.setDamage(damage);
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void tick(LivingEntity entity) {
        if(entity.getLocation().getBlock().getLightLevel() > 0) {
            if(remove) entity.remove();
        } else entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_CAT_PURR, 0.5f, 0);
    }
}
