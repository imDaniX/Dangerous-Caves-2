package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.mobs.MobBase;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.random.Rng;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
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

public class CaveGolem extends MobBase implements Listener {
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0);
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 30, 0);
    private static final PotionEffect CONFUSION = new PotionEffect(PotionEffectType.CONFUSION, 20, 0);
    private static final PotionEffect SLOW_PL = new PotionEffect(PotionEffectType.SLOW, 40, 1);

    private static final ItemStack CHESTPLATE = Materials.getColored(EquipmentSlot.CHEST, 105, 105, 105);;
    private static final ItemStack LEGGINGS = Materials.getColored(EquipmentSlot.LEGS, 105, 105, 105);
    private static final ItemStack BOOTS = Materials.getColored(EquipmentSlot.FEET, 105, 105, 105);

    private static final Set<Material> PICKAXES = new Materials.Builder().with(
            Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE
    ).with(
            "NETHERITE_PICKAXE"
    ).with(
            Materials.or("GOLDEN_PICKAXE", "GOLD_PICKAXE")
    ).build(true);

    private final Plugin plugin;
    private final MobsManager mobs;

    private final List<ItemStack> heads;
    private Set<Material> materials;

    private boolean slow;
    private boolean distract;
    private double nonPickaxeModifier;
    private double pickaxeModifier;
    private double mobModifier;

    private double breakChance;
    private Listener breakListener;

    public CaveGolem(MobsManager mobs) {
        super(EntityType.SKELETON, "cave-golem", 3, 35d);
        this.plugin = mobs.getPlugin();
        this.mobs = mobs;
        this.heads = new ArrayList<>();
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        slow = cfg.getBoolean("slowness", true);
        distract = cfg.getBoolean("distract-attack", true);
        nonPickaxeModifier = cfg.getDouble("nonpickaxe-modifier", 0.07);
        pickaxeModifier = cfg.getDouble("pickaxe-modifier", 2.0);
        mobModifier = cfg.getDouble("damage-modifier", 2.0);

        materials = Materials.getSet(cfg.getStringList("variants"));
        heads.clear();
        materials.forEach(m -> heads.add(new ItemStack(m)));

        if (heads.isEmpty()) heads.add(new ItemStack(Material.STONE));

        if (!materials.isEmpty() && (breakChance = cfg.getDouble("spawn-from-block") / 100) > 0) {
            if (breakListener == null) {
                Bukkit.getPluginManager().registerEvents(breakListener = new Listener() {
                    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
                    public void onBreak(BlockBreakEvent event) {
                        Block block = event.getBlock();
                        if (mobs.checkWorld(block.getWorld().getName())
                                && materials.contains(block.getType()) && Rng.chance(breakChance)
                                && Regions.INSTANCE.check(CheckType.ENTITY, block.getLocation())) {
                            event.setDropItems(false);
                            event.setExpToDrop(0);
                            mobs.spawn(CaveGolem.this, block.getLocation())
                                    .getEquipment().setHelmet(new ItemStack(block.getType()));
                        }
                    }
                }, plugin);
            }
        } else if (breakListener != null) {
            HandlerList.unregisterAll(breakListener);
            breakListener = null;
        }
    }

    @Override
    public void setup(LivingEntity entity) {
        EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInMainHand(null);
        equipment.setHelmet(Rng.randomElement(heads));  equipment.setHelmetDropChance(1);
        equipment.setChestplate(CHESTPLATE);            equipment.setChestplateDropChance(0);
        equipment.setLeggings(LEGGINGS);                equipment.setLeggingsDropChance(0);
        equipment.setBoots(BOOTS);                      equipment.setBootsDropChance(0);
        entity.setSilent(true);
        if (slow) entity.addPotionEffect(SLOW);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (isThis(event.getDamager())) {
            if (!(event.getEntity() instanceof LivingEntity entity)) return;
            Locations.playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 2, 0.5f);
            if (distract) {
                entity.addPotionEffect(BLINDNESS);
                entity.addPotionEffect(SLOW_PL);
                entity.addPotionEffect(CONFUSION);
            }
            event.setDamage(event.getDamage() * mobModifier);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (isThis(entity)) {
            if (event instanceof EntityDamageByEntityEvent enEvent) {
                if (enEvent.getDamager() instanceof Player player) {
                    if (PICKAXES.contains(player.getInventory().getItemInMainHand().getType())) {
                        Locations.playSound(entity.getLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.HOSTILE, 0.6f, 1);
                        event.setDamage(event.getDamage() * pickaxeModifier);
                        return;
                    } else {
                        Locations.playSound(entity.getLocation(), Sound.ITEM_SHIELD_BREAK, SoundCategory.HOSTILE, 1, 1);
                    }
                }
            } else {
                Locations.playSound(entity.getLocation(), Sound.BLOCK_STONE_BREAK, SoundCategory.HOSTILE, 2, 0.6f);
            }
            event.setDamage(event.getDamage() * nonPickaxeModifier);
        }
    }
}
