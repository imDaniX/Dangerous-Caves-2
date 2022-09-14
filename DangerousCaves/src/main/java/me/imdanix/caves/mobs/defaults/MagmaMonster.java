package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.compatibility.VMaterial;
import me.imdanix.caves.mobs.CustomMob;
import me.imdanix.caves.mobs.MobBase;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.random.Rng;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MagmaMonster extends MobBase implements CustomMob.Ticking, Listener {
    private static final PotionEffect FIRE_RESISTANCE = new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
            Integer.MAX_VALUE, 1, false, false);
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY,
            Integer.MAX_VALUE, 1, false, false);

    private static final ItemStack CHESTPLATE;
    private static final ItemStack LEGGINGS;
    private static final ItemStack BOOTS;
    private static final ItemStack POWDER;
    static {
        CHESTPLATE = Materials.getColored(EquipmentSlot.CHEST, 115, 57, 34);
        LEGGINGS = Materials.getColored(EquipmentSlot.LEGS, 115, 57, 34);
        BOOTS = Materials.getColored(EquipmentSlot.FEET, 115, 57, 34);
        POWDER = new ItemStack(Material.BLAZE_POWDER);
    }

    private double fireChance;
    private double magmaChance;
    private boolean extinguishDamage;
    private boolean requiresTarget;

    public MagmaMonster() {
        super(EntityType.ZOMBIE, "magma-monster", 4);
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        fireChance = cfg.getDouble("fire-chance", 7.14) / 100;
        magmaChance = cfg.getDouble("magma-chance", 3.57) / 100;
        extinguishDamage = cfg.getBoolean("extinguished-damage", false);
        requiresTarget = cfg.getBoolean("requires-target", true);
    }

    @Override
    public void setup(LivingEntity entity) {
        entity.setFireTicks(Integer.MAX_VALUE);
        entity.setSilent(true);
        entity.setCanPickupItems(false);

        EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInMainHand(POWDER);
        equipment.setItemInOffHand(POWDER);
        equipment.setHelmet(null);
        equipment.setChestplate(CHESTPLATE);    equipment.setChestplateDropChance(0);
        equipment.setLeggings(LEGGINGS);        equipment.setLeggingsDropChance(0);
        equipment.setBoots(BOOTS);              equipment.setBootsDropChance(0);

        entity.addPotionEffect(FIRE_RESISTANCE);
        entity.addPotionEffect(INVISIBILITY);
        entity.setFireTicks(20);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (isThis(event.getDamager()) && Rng.nextBoolean())
            event.getEntity().setFireTicks(60);
    }

    @Override
    public void tick(LivingEntity entity) {
        if (extinguishDamage && entity.getFireTicks() > 0) {
            entity.damage(0.1);
        } else {
            entity.setFireTicks(20);
        }

        if (requiresTarget && ((Monster)entity).getTarget() == null) return;

        boolean fire;
        boolean magma;
        if (((fire = fireChance > 0 && Rng.chance(fireChance)) | (magma = magmaChance > 0 && Rng.chance(magmaChance))) &&
                !Regions.INSTANCE.check(CheckType.ENTITY, entity.getLocation()))
            return;

        if (fire) {
            Block block = entity.getLocation().getBlock();
            if (Materials.isAir(block.getType()) && block.getRelative(BlockFace.DOWN).getType().isSolid())
                block.setType(Material.FIRE, false);
        }

        if (magma) {
            Block block = entity.getLocation().subtract(0, 1, 0).getBlock();
            if (block.getType() != Material.BEDROCK && Materials.isCave(block.getType()))
                block.setType(VMaterial.MAGMA_BLOCK.get(), false);
        }
    }
}
