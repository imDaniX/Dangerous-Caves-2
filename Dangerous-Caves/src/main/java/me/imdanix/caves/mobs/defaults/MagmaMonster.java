package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.compatibility.VMaterial;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.mobs.TickableMob;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.PlayerAttackedEvent;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Configurable.Path("mobs.magma-monster")
public class MagmaMonster extends TickableMob implements Listener, Configurable {
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

    private int weight;
    private String name;
    private double fireChance;
    private double magmaChance;

    public MagmaMonster() {
        super(EntityType.ZOMBIE, "magma-monster");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", "&4Magma Monster"));
        fireChance = cfg.getDouble("fire-chance", 7.14) / 100;
        magmaChance = cfg.getDouble("magma-chance", 3.57) / 100;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        entity.addPotionEffect(FIRE_RESISTANCE);
        entity.addPotionEffect(INVISIBILITY);
        entity.setFireTicks(Integer.MAX_VALUE);
        entity.setSilent(true);
        entity.setCanPickupItems(false);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInMainHand(POWDER);
        equipment.setItemInOffHand(POWDER);
        equipment.setChestplate(CHESTPLATE);    equipment.setChestplateDropChance(0);
        equipment.setLeggings(LEGGINGS);        equipment.setLeggingsDropChance(0);
        equipment.setBoots(BOOTS);              equipment.setBootsDropChance(0);
    }

    @EventHandler
    public void onAttack(PlayerAttackedEvent event) {
        if(!isThis(event.getAttacker()) || Rnd.nextBoolean()) return;
        event.getPlayer().setFireTicks(60);
    }

    @Override
    public void tick(LivingEntity entity) {
        entity.setFireTicks(20);

        if(fireChance > 0 && Rnd.chance(fireChance)) {
            Block block = entity.getLocation().getBlock();
            if(Compatibility.isAir(block.getType()))
                block.setType(Material.FIRE, false);
        }
        if(magmaChance > 0 && Rnd.chance(magmaChance)) {
            Block block = entity.getLocation().subtract(0, 1, 0).getBlock();
            if(block.getType() != Material.BEDROCK && Compatibility.isCave(block.getType()))
                block.setType(VMaterial.MAGMA_BLOCK.get(), false);
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
