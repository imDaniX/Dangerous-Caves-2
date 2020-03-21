package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.TickableMob;
import com.github.evillootlye.caves.util.Materials;
import com.github.evillootlye.caves.util.Utils;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Configurable.Path("mobs.dead-miner")
public class DeadMiner extends TickableMob implements Configurable, Listener {
    private int weight;
    private String name;
    private boolean torches, redTorches;
    private double dropChance;
    private ItemStack head;
    private List<Material> items;

    public DeadMiner() {
        super(EntityType.ZOMBIE, "dead-miner");
        items = new ArrayList<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", "&4Dead Miner"));
        torches = cfg.getBoolean("place-torches", true);
        redTorches = cfg.getBoolean("redstone-torches", false);
        dropChance = cfg.getDouble("drop-chance", 16.67) / 100;
        head = Materials.getHeadFromValue(cfg.getString("head-value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE5MzdiY2Q1YmVlYWEzNDI0NDkxM2YyNzc1MDVlMjlkMmU2ZmIzNWYyZTIzY2E0YWZhMmI2NzY4ZTM5OGQ3MyJ9fX0="));

        items.clear();
        List<String> itemsCfg = cfg.getStringList("drop-items");
        for(String materialStr : itemsCfg) {
            Material material = Material.getMaterial(materialStr.toUpperCase());
            if(material != null) items.add(material);
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if(!isThis(event.getEntity()) || event.getDamage() < 1) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if(dropChance > 0 && !items.isEmpty() && Rnd.chance(dropChance))
            entity.getWorld().dropItemNaturally(
                    entity.getLocation(),
                    new ItemStack(items.get(Rnd.nextInt(items.size())))
            );
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(head);
        equipment.setHelmetDropChance(0);
        equipment.setItemInMainHand(new ItemStack(Rnd.nextBoolean() ? Material.IRON_PICKAXE : Material.STONE_PICKAXE));
        if(Rnd.nextBoolean())
            equipment.setChestplate(new ItemStack(Rnd.nextBoolean() ? Material.CHAINMAIL_CHESTPLATE : Material.LEATHER_CHESTPLATE));
        if(Rnd.nextBoolean())
            equipment.setBoots(new ItemStack(Rnd.nextBoolean() ? Material.CHAINMAIL_BOOTS : Material.LEATHER_BOOTS));
        if(torches) equipment.setItemInOffHand(new ItemStack(redTorches ? Material.REDSTONE_TORCH : Material.TORCH));
        entity.setCanPickupItems(false);
    }

    @Override
    public void tick(LivingEntity entity) {
        if(!torches) return;
        Location loc = entity.getLocation();
        Block block = loc.getBlock();
        if(block.getLightLevel() > 0) return;
        if(Materials.isAir(block.getType()) && Materials.isCave(block.getRelative(BlockFace.DOWN).getType()))
            block.setType(redTorches ? Material.REDSTONE_TORCH : Material.TORCH);
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
