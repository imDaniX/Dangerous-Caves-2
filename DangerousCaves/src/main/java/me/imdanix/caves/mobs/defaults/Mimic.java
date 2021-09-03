package me.imdanix.caves.mobs.defaults;

import io.papermc.lib.PaperLib;
import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.compatibility.VMaterial;
import me.imdanix.caves.compatibility.VSound;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.mobs.TickingMob;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.Regions;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rng;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// TODO: Other blocks like furnace?
public class Mimic extends TickingMob implements Listener {
    private static final boolean CHUNK_IS_HOLDER = PaperLib.getMinecraftVersion() >= 14 && PersistentDataHolder.class.isAssignableFrom(Chunk.class);

    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 60, 1);
    private static final ItemStack CHEST = new ItemStack(Material.CHEST);
    private static final ItemStack CHESTPLATE = Materials.getColored(EquipmentSlot.CHEST, 194, 105, 18);
    private static final ItemStack LEGGINGS = Materials.getColored(EquipmentSlot.LEGS, 194, 105, 18);
    private static final ItemStack BOOTS = Materials.getColored(EquipmentSlot.FEET, 194, 105, 18);
    private static final ItemStack PLANKS = new ItemStack(VMaterial.SPRUCE_PLANKS.get());

    private final MobsManager mobsManager;
    private final NamespacedKey chunkKey;
    private final List<Material> items;
    private boolean clean;
    private Listener unloadListener;

    public Mimic(MobsManager mobsManager) {
        super(EntityType.WITHER_SKELETON, "mimic", 0, 30d);
        this.mobsManager = mobsManager;
        items = new ArrayList<>();
        chunkKey = new NamespacedKey(mobsManager.getPlugin(), "mimic-count");
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        items.clear();
        List<String> itemsCfg = cfg.getStringList("drop-items");
        for (String materialStr : itemsCfg) {
            Material material = Material.getMaterial(materialStr.toUpperCase(Locale.ENGLISH));
            if (material != null) items.add(material);
        }
        if (clean = cfg.getBoolean("remove-on-unload", false)) {
            if (unloadListener == null) {
                Bukkit.getPluginManager().registerEvents(unloadListener = new Listener() {
                    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
                    public void onUnload(ChunkUnloadEvent event) {
                        if (CHUNK_IS_HOLDER) {
                            PersistentDataContainer container = event.getChunk().getPersistentDataContainer();
                            if (!container.has(chunkKey, PersistentDataType.INTEGER)) return;
                            int amount = container.getOrDefault(chunkKey, PersistentDataType.INTEGER, Integer.MAX_VALUE);
                            for (BlockState tile : event.getChunk().getTileEntities()) {
                                if (!(tile instanceof Chest)) continue;
                                String tag = Compatibility.getTag(tile);
                                if (tag == null || !tag.startsWith("mimic")) continue;
                                tile.setType(Material.AIR);
                                if (--amount == 0) break;
                            }
                            container.remove(chunkKey);
                        } else for (BlockState tile : event.getChunk().getTileEntities()) {
                            if (!(tile instanceof Chest)) continue;
                            String tag = Compatibility.getTag(tile);
                            if (tag == null || !tag.startsWith("mimic")) continue;
                            tile.setType(Material.AIR);
                        }
                    }
                }, mobsManager.getPlugin());
            }
        } else if (unloadListener != null) {
            HandlerList.unregisterAll(unloadListener);
            unloadListener = null;
        }
    }

    @Override
    public void setup(LivingEntity entity) {
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.CHEST) return;
        Player player = event.getPlayer();
        for (BlockFace face : Locations.HORIZONTAL_FACES) {
            Block rel = block.getRelative(face);
            if (rel.getType() == Material.CHEST && openMimic(rel,player)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() == Material.CHEST && openMimic(block, event.getPlayer())) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setCancelled(true);
        }
    }

    private boolean openMimic(Block block, Player player) {
        String tag = Compatibility.getTag(block.getState());
        if (tag == null || !tag.startsWith("mimic")) return false;
        if (block.getRelative(BlockFace.UP).getType().isSolid()) return true;
        block.setType(Material.AIR);
        double health = Utils.getDouble(tag.substring(6), this.health); // Safe because will be defined anyway
        if (health <= 0) health = 1;
        Location loc = block.getLocation();
        LivingEntity entity = mobsManager.spawn(this, loc.add(0.5, 0, 0.5));
        Utils.setMaxHealth(entity, this.health);
        entity.setHealth(Math.min(health, this.health));
        Locations.playSound(loc, VSound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR.get(), 1f, 0.5f);
        player.addPotionEffect(BLINDNESS);
        ((Monster)entity).setTarget(player);
        return true;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (isThis(entity)) Locations.playSound(entity.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1f, 0.2f);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (isThis(event.getEntity())) {
            Locations.playSound(event.getEntity().getLocation(), VSound.BLOCK_ENDER_CHEST_CLOSE.get(), SoundCategory.HOSTILE, 1f, 0.2f);
            List<ItemStack> drops = event.getDrops();
            drops.clear();
            drops.add(CHEST);
            if (!items.isEmpty()) drops.add(new ItemStack(Rng.randomElement(items)));
        }
    }

    @Override
    public void tick(LivingEntity entity) {
        Block block = entity.getLocation().getBlock();
        if (((Monster)entity).getTarget() == null && Materials.isAir(block.getType()) &&
                Regions.INSTANCE.check(CheckType.ENTITY, entity.getLocation())) {
            for (BlockFace face : Locations.HORIZONTAL_FACES)
                if (block.getRelative(face).getType() == Material.CHEST) return;
            block.setType(Material.CHEST, false);
            Compatibility.rotate(block, Locations.HORIZONTAL_FACES[Rng.nextInt(4)]);
            Compatibility.setTag(block.getState(), "mimic-" + entity.getHealth());
            entity.remove();

            if (clean && CHUNK_IS_HOLDER) {
                PersistentDataContainer container = block.getChunk().getPersistentDataContainer();
                container.set(chunkKey, PersistentDataType.INTEGER, container.getOrDefault(chunkKey, PersistentDataType.INTEGER, 0) + 1);
            }
        }
    }
}