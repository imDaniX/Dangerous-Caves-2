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
import me.imdanix.caves.mobs.TickingMob;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Watcher extends TickingMob implements Listener {
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, 30, 4);
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 80, 2);
    private static final Vector ZERO_VECTOR = new Vector(0, 0, 0);

    private String name;
    private double health;

    private ItemStack head;

    public Watcher() {
        super(EntityType.HUSK, "watcher", 7);
    }

    @Override
    public void configure(ConfigurationSection cfg) {
        name = Utils.clr(cfg.getString("name", "&4Watcher"));
        health = cfg.getDouble("health", 20);

        head = Compatibility.getHeadFromValue(cfg.getString("head-value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDI5MzhmMjQxZDc0NDMzZjcyZjVjMzljYjgzYThlNWZmN2UxNzdiYTdjYjQyODY5ZGI2NGUzMDc5MTAyYmZjNSJ9fX0="));
    }

    @Override
    public void setup(LivingEntity entity) {
        if (!name.isEmpty()) entity.setCustomName(name);
        Utils.setMaxHealth(entity, health);

        entity.setSilent(true);
        entity.setCanPickupItems(false);
        entity.addPotionEffect(INVISIBILITY);

        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(head);      equipment.setHelmetDropChance(0);
        equipment.setChestplate(null);
        equipment.setLeggings(null);
        equipment.setBoots(null);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (isThis(event.getEntity()))
            Locations.playSound(event.getEntity().getLocation(), Sound.ENTITY_SLIME_SQUISH, 1, 1.1f);
    }

    @Override
    public void tick(LivingEntity entity) {
        LivingEntity target = ((Monster)entity).getTarget();
        if (target instanceof Player) {
            if (Locations.isLookingAt(target, entity)) return;
            Location loc = target.getLocation().add(target.getLocation().getDirection());
            loc.setYaw(-loc.getYaw());
            entity.teleport(loc);
            target.setVelocity(ZERO_VECTOR);
            target.addPotionEffect(SLOW);
            target.addPotionEffect(BLINDNESS);
            Locations.playSound(target.getEyeLocation(), Sound.ENTITY_GHAST_HURT, 1, 2);
        }
    }
}
