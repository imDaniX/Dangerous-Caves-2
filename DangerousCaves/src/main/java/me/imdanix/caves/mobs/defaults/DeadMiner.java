package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.compatibility.VMaterial;
import me.imdanix.caves.mobs.TickingMob;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rng;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeadMiner extends TickingMob implements Listener {
    private String name;
    private double health;

    private boolean requiresTarget;
    private boolean torches;
    private boolean redTorches;
    private double dropChance;
    private ItemStack head;
    private List<Material> items;
    private PotionEffect cooldownEffect;

    public DeadMiner() {
        super(EntityType.ZOMBIE, "dead-miner", 10);
        items = new ArrayList<>();
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        name = Utils.clr(cfg.getString("name", "&4Dead Miner"));
        health = cfg.getDouble("health", 22);

        requiresTarget = cfg.getBoolean("requires-target", true);
        torches = cfg.getBoolean("place-torches", true);
        redTorches = cfg.getBoolean("redstone-torches", false);
        dropChance = cfg.getDouble("drop-chance", 16.67) / 100;
        head = Compatibility.getHeadFromValue(cfg.getString("head-value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE5MzdiY2Q1YmVlYWEzNDI0NDkxM2YyNzc1MDVlMjlkMmU2ZmIzNWYyZTIzY2E0YWZhMmI2NzY4ZTM5OGQ3MyJ9fX0="));

        items.clear();
        List<String> itemsCfg = cfg.getStringList("drop-items");
        for (String materialStr : itemsCfg) {
            Material material = Material.getMaterial(materialStr.toUpperCase(Locale.ENGLISH));
            if (material != null) items.add(material);
        }

        int cooldown = cfg.getInt("torches-cooldown", 12);
        if (cooldown <= 0) {
            cooldownEffect = null;
        } else {
            cooldownEffect = new PotionEffect(PotionEffectType.CONFUSION, cooldown*20, 0, true, false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!isThis(event.getEntity()) || event.getDamage() < 1) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (dropChance > 0 && !items.isEmpty() && Rng.chance(dropChance))
            entity.getWorld().dropItemNaturally(
                    entity.getLocation(),
                    new ItemStack(Rng.randomElement(items))
            );
    }

    @Override
    public void setup(LivingEntity entity) {
        if (!name.isEmpty()) entity.setCustomName(name);
        Utils.setMaxHealth(entity, health);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(head); equipment.setHelmetDropChance(0);
        equipment.setItemInMainHand(new ItemStack(Rng.nextBoolean() ? Material.IRON_PICKAXE : Material.STONE_PICKAXE));
        if (Rng.nextBoolean())
            equipment.setChestplate(new ItemStack(Rng.nextBoolean() ? Material.CHAINMAIL_CHESTPLATE : Material.LEATHER_CHESTPLATE));
        if (Rng.nextBoolean())
            equipment.setBoots(new ItemStack(Rng.nextBoolean() ? Material.CHAINMAIL_BOOTS : Material.LEATHER_BOOTS));
        if (torches) equipment.setItemInOffHand(new ItemStack(redTorches ? VMaterial.REDSTONE_TORCH.get() : Material.TORCH));
        entity.setCanPickupItems(false);
    }

    @Override
    public void tick(LivingEntity entity) {
        if (!torches || entity.hasPotionEffect(PotionEffectType.CONFUSION)) return;
        Block block = entity.getLocation().getBlock();

        if (block.getLightLevel() > 0 || (requiresTarget && ((Monster)entity).getTarget() == null) ||
                !Regions.INSTANCE.check(CheckType.ENTITY, block.getLocation()))
            return;

        if (Materials.isAir(block.getType()) && Materials.isCave(block.getRelative(BlockFace.DOWN).getType())) {
            block.setType(redTorches ? VMaterial.REDSTONE_TORCH.get() : Material.TORCH, false);
            Locations.playSound(block.getLocation(), Sound.BLOCK_WOOD_PLACE, 1, 1);
            if (cooldownEffect != null)
                entity.addPotionEffect(cooldownEffect);
        }
    }
}
