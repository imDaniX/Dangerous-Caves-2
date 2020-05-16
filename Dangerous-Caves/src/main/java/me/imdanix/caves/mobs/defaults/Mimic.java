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
import me.imdanix.caves.compatibility.VSound;
import me.imdanix.caves.mobs.TickingMob;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Mimic extends TickingMob implements Listener {
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 60, 1);
    private final List<Material> items;

    private String name;
    private double health;

    private static final ItemStack CHEST;
    private static final ItemStack CHESTPLATE;
    private static final ItemStack LEGGINGS;
    private static final ItemStack BOOTS;
    private static final ItemStack PLANKS;
    static {
        CHEST = new ItemStack(Material.CHEST);
        CHESTPLATE = Materials.getColored(EquipmentSlot.CHEST, 194, 105, 18);
        LEGGINGS = Materials.getColored(EquipmentSlot.LEGS, 194, 105, 18);
        BOOTS = Materials.getColored(EquipmentSlot.FEET, 194, 105, 18);
        PLANKS = new ItemStack(VMaterial.SPRUCE_PLANKS.get());
    }

    public Mimic() {
        super(EntityType.WITHER_SKELETON, "mimic", 0);
        items = new ArrayList<>();
    }

    @Override
    public void configure(ConfigurationSection cfg) {
        name = Utils.clr(cfg.getString("name", "&4Mimic"));
        health = cfg.getDouble("health", 20);

        items.clear();
        List<String> itemsCfg = cfg.getStringList("drop-items");
        for(String materialStr : itemsCfg) {
            Material material = Material.getMaterial(materialStr.toUpperCase());
            if(material != null) items.add(material);
        }
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        Utils.setMaxHealth(entity, health);

        entity.setSilent(true);
        entity.setCanPickupItems(false);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(CHEST);
        equipment.setItemInMainHand(PLANKS);
        equipment.setItemInOffHand(PLANKS);
        equipment.setChestplate(CHESTPLATE);    equipment.setChestplateDropChance(0);
        equipment.setLeggings(LEGGINGS);        equipment.setLeggingsDropChance(0);
        equipment.setBoots(BOOTS);              equipment.setBootsDropChance(0);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block.getType() == Material.CHEST && !block.getRelative(BlockFace.UP).getType().isSolid()) {
            String tag = Compatibility.getTag(block);
            if(tag == null || !tag.startsWith("mimic-")) return;
            double health = Double.parseDouble(tag.substring(6));
            event.setCancelled(true);
            Location loc = block.getLocation();
            LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc.add(0.5, 0, 0.5), EntityType.WITHER_SKELETON);
            setup(entity);
            entity.setHealth(health);
            Player player = event.getPlayer();
            loc.getWorld().playSound(loc, VSound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR.get(), 1f, 0.5f);
            player.addPotionEffect(BLINDNESS);
            ((Monster)entity).setTarget(player);
            block.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if(isThis(entity)) entity.getLocation().getWorld().playSound(entity.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1f, 0.2f);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(isThis(event.getEntity())) {
            event.setDeathSound(VSound.BLOCK_ENDER_CHEST_CLOSE.get());
            event.setDeathSoundPitch(0.2f);
            List<ItemStack> items = event.getDrops();
            items.clear();
            items.add(CHEST);
            items.add(new ItemStack(Rnd.randomItem(this.items)));
        }
    }

    @Override
    public void tick(LivingEntity entity) {
        Block block = entity.getLocation().getBlock();
        if(((Monster)entity).getTarget() == null && Compatibility.isAir(block.getType())) {
            for(BlockFace face : Locations.HORIZONTAL_FACES)
                if(block.getRelative(face).getType() == Material.CHEST) return;
            block.setType(Material.CHEST, false);
            Compatibility.rotate(block, Locations.HORIZONTAL_FACES[Rnd.nextInt(4)]);
            Compatibility.setTag(block, "mimic-" + entity.getHealth());
            entity.remove();
        }
    }
}