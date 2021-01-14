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

import me.imdanix.caves.mobs.AbstractMob;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CaveGolem extends AbstractMob implements Listener {
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0);
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 30, 0);
    private static final PotionEffect CONFUSION = new PotionEffect(PotionEffectType.CONFUSION, 20, 0);
    private static final PotionEffect SLOW_PL = new PotionEffect(PotionEffectType.SLOW, 40, 1);

    private static final ItemStack CHESTPLATE;
    private static final ItemStack LEGGINGS;
    private static final ItemStack BOOTS;
    static {
        CHESTPLATE = Materials.getColored(EquipmentSlot.CHEST, 105, 105, 105);
        LEGGINGS = Materials.getColored(EquipmentSlot.LEGS, 105, 105, 105);
        BOOTS = Materials.getColored(EquipmentSlot.FEET, 105, 105, 105);
    }

    private final Plugin plugin;
    private final MobsManager mobs;

    private String name;
    private double health;

    private final List<ItemStack> heads;
    private Set<Material> materials;

    private boolean slow;
    private boolean distract;
    private double nonPickaxe;
    private double damageModifier;

    private double breakChance;
    private Listener breakListener;

    public CaveGolem(MobsManager mobs) {
        super(EntityType.SKELETON, "cave-golem", 3);
        this.plugin = mobs.getPlugin();
        this.mobs = mobs;
        this.heads = new ArrayList<>();
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        name = Utils.clr(cfg.getString("name", "&4Dead Miner"));
        health = cfg.getDouble("health", 35);

        slow = cfg.getBoolean("slowness", true);
        distract = cfg.getBoolean("distract-attack", true);
        nonPickaxe = cfg.getDouble("nonpickaxe-modifier", 0.07);
        damageModifier = cfg.getDouble("damage-modifier", 2.0);

        materials = Materials.getSet(cfg.getStringList("variants"));
        heads.clear();
        materials.forEach(m -> heads.add(new ItemStack(m)));

        if (heads.isEmpty()) heads.add(new ItemStack(Material.STONE));

        if (!materials.isEmpty() && (breakChance = cfg.getDouble("spawn-from-block")/100) > 0) {
            if (breakListener == null) {
                Bukkit.getPluginManager().registerEvents(new Listener() {
                    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
                    public void onBreak(BlockBreakEvent event) {
                        Block block = event.getBlock();
                        if (materials.contains(block.getType()) && Rnd.chance(breakChance)
                                && Regions.INSTANCE.check(CheckType.ENTITY, block.getLocation())) {
                            event.setDropItems(false);
                            event.setExpToDrop(0);
                            mobs.spawn(CaveGolem.this, block.getLocation())
                                    .getEquipment().setHelmet(new ItemStack(block.getType()));
                        }
                    }
                }, plugin);
            }
        } else {
            if (breakListener != null) {
                HandlerList.unregisterAll(breakListener);
                breakListener = null;
            }
        }
    }

    @Override
    public void setup(LivingEntity entity) {
        if (!name.isEmpty()) entity.setCustomName(name);
        Utils.setMaxHealth(entity, health);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInMainHand(null);
        equipment.setHelmet(Rnd.randomElement(heads)); equipment.setHelmetDropChance(1);
        equipment.setChestplate(CHESTPLATE);        equipment.setChestplateDropChance(0);
        equipment.setLeggings(LEGGINGS);            equipment.setLeggingsDropChance(0);
        equipment.setBoots(BOOTS);                  equipment.setBootsDropChance(0);
        entity.setSilent(true);
        if (slow) entity.addPotionEffect(SLOW);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (isThis(event.getDamager())) {
            if (!(event.getEntity() instanceof LivingEntity)) return;
            LivingEntity entity = (LivingEntity) event.getEntity();
            Locations.playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.HOSTILE, 2, 0.5f);
            if (distract) {
                entity.addPotionEffect(BLINDNESS);
                entity.addPotionEffect(SLOW_PL);
                entity.addPotionEffect(CONFUSION);
            }
            event.setDamage(event.getDamage() * damageModifier);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (isThis(event.getEntity())) {
            Locations.playSound(event.getEntity().getLocation(), Sound.BLOCK_STONE_BREAK, SoundCategory.HOSTILE, 2, 0.6f);
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent enEvent = (EntityDamageByEntityEvent) event;
                if (enEvent.getDamager() instanceof Player) {
                    Player player = (Player) enEvent.getDamager();
                    if (player.getInventory().getItemInMainHand().getType().name().endsWith("PICKAXE"))
                        return;
                }
            }
            event.setDamage(event.getDamage() * nonPickaxe);
        }
    }
}
