/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2021  imDaniX
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

import me.imdanix.caves.compatibility.VSound;
import me.imdanix.caves.mobs.AbstractMob;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.PlayerAttackedEvent;
import me.imdanix.caves.util.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HungeringDarkness extends AbstractMob implements Listener {
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3, false, false);

    private String name;
    private double damage;
    private boolean remove;
    private boolean vision;
    private boolean deathSound;

    public HungeringDarkness() {
        super(EntityType.VEX, "hungering-darkness", 8);
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        name = Utils.clr(cfg.getString("name", ""));
        damage = cfg.getDouble("damage", 200);
        remove = cfg.getBoolean("remove-on-light", false);
        vision = cfg.getBoolean("night-vision", false);
        deathSound = cfg.getBoolean("death-sound", true);
    }

    @Override
    public boolean canSpawn(Location location) {
        return location.getBlock().getLightLevel() == 0;
    }

    @Override
    public void setup(LivingEntity entity) {
        if (!name.isEmpty()) entity.setCustomName(name);
        entity.setCustomNameVisible(false);
        entity.setInvulnerable(true);
        entity.setCollidable(false);

        entity.addPotionEffect(INVISIBILITY);
        entity.addPotionEffect(SLOW);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        if (!isThis(event.getEntity()) || target == null) return;
        if (target.getLocation().getBlock().getLightLevel() > 0 && (
                !vision ||
                !(target instanceof LivingEntity) ||
                ((LivingEntity)target).hasPotionEffect(PotionEffectType.NIGHT_VISION)
            )) {
            event.setCancelled(true);
            die(event.getEntity());
        }
    }

    @EventHandler
    public void onAttack(PlayerAttackedEvent event) {
        if (!isThis(event.getAttacker())) return;
        Player player = event.getPlayer();
        if (player.getLocation().getBlock().getLightLevel() > 0 && (
                !vision ||
                player.hasPotionEffect(PotionEffectType.NIGHT_VISION)
            )) {
            event.setCancelled(true);
            die(event.getAttacker());
        } else event.setDamage(damage);
    }

    private void die(Entity entity) {
        if (!remove) return;
        if (deathSound) {
            Locations.playSound(entity.getLocation(), VSound.ENTITY_PHANTOM_SWOOP.get(), 1f, 0.8f);
        }
        entity.remove();
    }
}
