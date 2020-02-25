package com.github.evillootlye.caves;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.Switch;
import org.bukkit.block.data.type.Switch.Face;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Deprecated
public class DangerousCavesOld implements Listener, CommandExecutor {

    public static DangerousCavesOld INSTANCE;
    public final FileConfiguration config = getConfig();

    public static List<String> itemcustom = new ArrayList<>();

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();
    private final Random randor = new Random();
    private static List<String> worlds = new ArrayList<>();
    private final List<Entity> effectEnts = new ArrayList<>();
    private boolean caveins = false;
    private boolean hungerdark = false;
    private boolean cavetemp = false;
    private boolean caveage = false;
    private boolean caveents = false;
    private static final List<String> mobNames = new ArrayList<>();
    private static List<String> hotmessage = new ArrayList<>();
    private int damage = 0;
    
    public FileConfiguration getConfig() {
        return DangerousCaves.INSTANCE.getConfig();
    }

    public void saveConfig() {
        DangerousCaves.INSTANCE.saveConfig();
    }

    public void onEnable() {
        createConfigFol();
        Bukkit.getPluginManager().registerEvents(this, DangerousCaves.INSTANCE);

        INSTANCE = this;
        caveins = config.getBoolean("Enable Cave-Ins ");
        hungerdark = config.getBoolean("Enable Hungering Darkness ");
        cavetemp = config.getBoolean("Enable Cave Temperature ");
        caveage = config.getBoolean("Enable Cave Aging ");
        caveents = config.getBoolean("Enable Cave Monsters ");
        mobNames.add("The Darkness");
        mobNames.add(config.getString("Magma Monster = "));
        mobNames.add(config.getString("Crying Bat = "));
        mobNames.add(config.getString("Lava Creeper = "));
        mobNames.add(config.getString("TnT Creeper = "));
        mobNames.add(config.getString("Watcher = "));
        mobNames.add(config.getString("Smoke Demon = "));
        mobNames.add(config.getString("Alpha Spider = "));
        mobNames.add(config.getString("Dead Miner = "));
        mobNames.add(config.getString("Hexed Armor = "));
        itemcustom = config.getStringList("Items that can spawn in chests ");
        hotmessage = config.getStringList("Temperature Messages ");
        worlds = config.getStringList("Enabled Worlds - If Left Blank Will Just Use default world ");
        int d = config.getInt("Hungering Darkness Damage ");
        if (d > 200) {
            damage = 200;
        } else if (d < 0) {
            damage = 0;
        } else {
            damage = d;
        }
        if (worlds.size() == 0) {
            boolean hasWorldR = false;
            for (World w : Bukkit.getWorlds()) {
                if (w.getName().equals("world")) {
                    hasWorldR = true;
                }
            }
            if (hasWorldR) {
                worlds.add("world");
            } else {
                worlds.add(Bukkit.getWorlds().get(0).getName());
            }
        }
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncRepeatingTask(DangerousCaves.INSTANCE, () -> {
            if (hungerdark || caveents) {
                betterEffectLooper();
            }
        }, 0L, /* 600 */((long) 3));
        scheduler.scheduleSyncRepeatingTask(DangerousCaves.INSTANCE, () -> {
            try {
                for (String namew : worlds) {
                    World wor = Bukkit.getWorld(namew);
                    if (wor != null) {
                        if (caveage) {
                            for (Player p : wor.getPlayers()) {
                                doCaveStuff(p);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                console.sendMessage(ChatColor.DARK_RED + "Our worst fears were real.");
            }
        }, 0L, /* 600 */7000L);

    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        if(event.getEntityType() == EntityType.PLAYER && event.getDamager() instanceof LivingEntity) {
            PlayerAttackedEvent pEvent = new PlayerAttackedEvent((Player) event.getEntity(), (LivingEntity)event.getDamager());
            Bukkit.getPluginManager().callEvent(pEvent);
            event.setCancelled(pEvent.isCancelled());
        }
    }

    private static boolean getLookingAt2(LivingEntity player, LivingEntity player1) {
        Location eye = player.getEyeLocation();
        Vector toEntity = player1.getEyeLocation().toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());

        return dot > 0.70D;
    }

    private void createConfigFol() {
        config.addDefault("Enable Cave-Ins ", true);
        config.addDefault("Enable Hungering Darkness ", true);
        config.addDefault("Enable Ambient Sounds ", true);
        config.addDefault("Enable Cave Temperature ", true);
        config.addDefault("Enable Cave Aging ", true);
        config.addDefault("Enable Cave Monsters ", true);
        config.addDefault("Enable Cave Structures ", true);
        config.addDefault("Enable Monster Fixing ", false);
        config.addDefault("Enable Broken Monster Deletion ", false);
        config.addDefault("Cave Skulls ", true);
        config.addDefault("Enable Easter Egg ", true);
        config.addDefault("::::Higher equals lower chance!::::", "");
        config.addDefault("Cave Aging Chance ", 2);
        config.addDefault("Cave Aging Change Chance ", 39);
        config.addDefault("Cave Ambience Chance ", 4);
        config.addDefault("Cave Walk Temp Chance ", 1449);
        config.addDefault("Cave Break Block Temp Chance ", 1450);
        config.addDefault("Cave-In Chance ", 399);
        config.addDefault("Darkness Spawn Chance ", 1);
        config.addDefault("Cave Structure Chance ", 1);
        config.addDefault("Hungering Darkness Damage ", 200);
        config.addDefault("Monster Spawning Highest Y ", 50);
        config.addDefault("Monster Spawning Lowest Y ", 0);
        config.addDefault(":::::::::::::::::::::::::::::::::::", "");
        config.addDefault("Traps Rate ", 0);
        config.addDefault("Buildings Rate ", 0);
        config.addDefault("Boulder Rate ", 0);
        config.addDefault("Pillar Rate ", 0);
        config.addDefault(":::::::::::::::::::::::::::::::::::", "");
        config.addDefault("Spawn Crying Bat ", true);
        config.addDefault("Spawn Magma Monster ", true);
        config.addDefault("Spawn Lava Creeper ", true);
        config.addDefault("Spawn TnT Creeper ", true);
        config.addDefault("Spawn Watcher ", true);
        config.addDefault("Spawn Smoke Demon ", true);
        config.addDefault("Spawn Alpha Spider ", true);
        config.addDefault("Spawn Dead Miner ", true);
        config.addDefault("Spawn Hexed Armor ", true);
        config.addDefault(":::::Monster Names - no Blanks:::::", "");
        config.addDefault("Crying Bat = ", "Crying Bat");
        config.addDefault("Magma Monster = ", "Magma Monster");
        config.addDefault("Lava Creeper = ", "Lava Creeper");
        config.addDefault("TnT Creeper = ", "TnT Infused Creeper");
        config.addDefault("Watcher = ", "Watcher");
        config.addDefault("Smoke Demon = ", "Smoke Demon");
        config.addDefault("Alpha Spider = ", "Alpha Spider");
        config.addDefault("Dead Miner = ", "Dead Miner");
        config.addDefault("Hexed Armor = ", "Hexed Armor");
        config.addDefault(":::::::::::::::::::::::::::::::::::", "");
        config.addDefault("Crying Bat Chance ", 0);
        config.addDefault("Magma Monster Chance ", 0);
        config.addDefault("Lava Creeper Chance ", 0);
        config.addDefault("TnT Creeper Chance ", 0);
        config.addDefault("Watcher Chance ", 0);
        config.addDefault("Smoke Demon Chance ", 0);
        config.addDefault("Alpha Spider Chance ", 0);
        config.addDefault("Dead Miner Chance ", 0);
        config.addDefault("Hexed Armor Chance ", 0);
        config.addDefault(":::::::::::::::::::::::::::::::::::", "");
        List<String> listitem1 = new ArrayList<>();
        listitem1.add("Wow, it's hot down here.  I better bring something to cool myself off next time.");
        listitem1.add("It is realllyyy hot down here.");
        listitem1.add("I'm going to need something to cool myself off next time.");
        listitem1.add("Is it hot in here, or is it just me?");
        listitem1.add("I really need some ice.");
        config.addDefault("Temperature Messages ", listitem1);
        List<String> listitem2 = new ArrayList<>();
        boolean hasWorldR = false;
        for (World w : Bukkit.getWorlds()) {
            if (w.getName().equals("world")) {
                hasWorldR = true;
            }
        }
        if (hasWorldR) {
            listitem2.add("world");
        } else {
            listitem2.add(Bukkit.getWorlds().get(0).getName());
        }
        config.addDefault("Enabled Worlds - If Left Blank Will Just Use default world ", listitem2);
        List<String> listitem3 = new ArrayList<>();
        listitem3.add("SUGAR");
        listitem3.add("TORCH");
        listitem3.add("DIRT");
        config.addDefault("Items that can spawn in chests ", listitem3);
        config.addDefault("Enabled Worlds - If Left Blank Will Just Use default world ", listitem2);
        config.addDefault(":Below is used to store information", 1);
        config.addDefault(":Do Not Modify Anything Below This", 1);
        config.addDefault(":Doing So Can Result in Messed up Worlds", 1);
        config.addDefault("002roomx", -1);
        config.addDefault("002roomy", -1);
        config.addDefault("002roomz", -1);
        // config.getString("Dead Miner = ")
        config.options().copyDefaults(true);
        saveConfig();
    }

    @EventHandler
    public void setWorld(PlayerJoinEvent e) {
        // for(String namew : worlds) {
        // World wor = Bukkit.getWorld(namew);
        // wor = e.getPlayer().getWorld();
        // List<BlockPopulator> bp = new ArrayList<BlockPopulator>(wor.getPopulators());
        // for(BlockPopulator bps : bp) {
        // if(bps.toString().length()>=25) {
        // if(bps.toString().substring(0, 25).equals("mainPackage.CaveGenerator")) {
        // wor.getPopulators().remove(bps);
        // }
        // }
        // }
        // wor.getPopulators().clear();
        // wor.getPopulators().add(new CaveGenerator());
        // console.sendMessage("" + e.getPlayer().getWorld().getPopulators());
        // public World wor = null;
        boolean hasWorlds = false;
        if (!hasWorlds) {
            caveins = config.getBoolean("Enable Cave-Ins ");
            hungerdark = config.getBoolean("Enable Hungering Darkness ");
            cavetemp = config.getBoolean("Enable Cave Temperature ");
            caveage = config.getBoolean("Enable Cave Aging ");
            caveents = config.getBoolean("Enable Cave Monsters ");
        }
        // }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("getworldname")) {
            if (sender instanceof Player) {
                console.sendMessage(ChatColor.GREEN + "World Name = " + ChatColor.RESET + " "
                        + ((Player) sender).getWorld().getName());
                doCaveStuff(((Player) sender));
                return true;
            }
        }
        return true;
    }

    // The Darkness

    private String isMetad(LivingEntity e) {
        if (e.hasMetadata(config.getString("Watcher = "))) {
            return config.getString("Watcher = ");
        } else if (e.hasMetadata(config.getString("Magma Monster = "))) {
            return config.getString("Magma Monster = ");
        } else if (e.hasMetadata(config.getString("Lava Creeper = "))) {
            return config.getString("Lava Creeper = ");
        } else if (e.hasMetadata(config.getString("TnT Creeper = "))) {
            return config.getString("TnT Creeper = ");
        } else if (e.hasMetadata(config.getString("Smoke Demon = "))) {
            return config.getString("Smoke Demon = ");
        } else if (e.hasMetadata(config.getString("Alpha Spider = "))) {
            return config.getString("Alpha Spider = ");
        } else if (e.hasMetadata(config.getString("Dead Miner = "))) {
            return config.getString("Dead Miner = ");
        } else if (e.hasMetadata(config.getString("Hexed Armor = "))) {
            return config.getString("Hexed Armor = ");
        } else if (e.hasMetadata("The Darkness")) {
            return "The Darkness";
        }
        return "null";
    }

    private boolean fixMonster(LivingEntity e) {
        if (!isMetad(e).equals("null")) {
            String name = isMetad(e);
            if (!e.isDead()) {
                try {
                    if (name.equals(config.getString("Watcher = "))) {
                        ItemStack helmet = e.getEquipment().getHelmet();
                        if (helmet == null) {
                            e.setSilent(false);
                            if (e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                e.removePotionEffect(PotionEffectType.INVISIBILITY);
                            }
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Watcher = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Watcher = "))) {
                                e.removeMetadata(config.getString("Watcher = "), DangerousCaves.INSTANCE);
                            }
                            return true;
                        } else if (helmet.getType() != Material.PLAYER_HEAD) {
                            e.setSilent(false);
                            if (e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                e.removePotionEffect(PotionEffectType.INVISIBILITY);
                            }
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Watcher = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Watcher = "))) {
                                e.removeMetadata(config.getString("Watcher = "), DangerousCaves.INSTANCE);
                            }
                            return true;
                        }
                    } else if (name.equals(config.getString("TnT Creeper = "))) {
                        String cus = e.getCustomName();
                        if (e.getType() != EntityType.CREEPER) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("TnT Creeper = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("TnT Creeper = "))) {
                                e.removeMetadata(config.getString("TnT Creeper = "), DangerousCaves.INSTANCE);
                            }
                            return true;
                        } else if (cus == null) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("TnT Creeper = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("TnT Creeper = "))) {
                                e.removeMetadata(config.getString("TnT Creeper = "), DangerousCaves.INSTANCE);
                            }
                            return true;
                        } else if (!cus.equals(config.getString("TnT Creeper = "))) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("TnT Creeper = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("TnT Creeper = "))) {
                                e.removeMetadata(config.getString("TnT Creeper = "), DangerousCaves.INSTANCE);
                            }
                            return true;
                        }
                    } else if (name.equals(config.getString("Lava Creeper = "))) {
                        String cus = e.getCustomName();
                        if (e.getType() != EntityType.CREEPER) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Lava Creeper = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Lava Creeper = "))) {
                                e.removeMetadata(config.getString("Lava Creeper = "), DangerousCaves.INSTANCE);
                            }
                            return true;
                        } else if (cus == null) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Lava Creeper = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Lava Creeper = "))) {
                                e.removeMetadata(config.getString("Lava Creeper = "), DangerousCaves.INSTANCE);
                            }
                            return true;
                        } else if (!cus.equals(config.getString("Lava Creeper = "))) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Lava Creeper = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Lava Creeper = "))) {
                                e.removeMetadata(config.getString("Lava Creeper = "), DangerousCaves.INSTANCE);
                            }
                            return true;
                        }
                    } else if (name.equals(config.getString("Dead Miner = "))) {
                        ItemStack helmet = e.getEquipment().getHelmet();
                        if (helmet == null) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Dead Miner = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Dead Miner = "))) {
                                e.removeMetadata(config.getString("Dead Miner = "), DangerousCaves.INSTANCE);
                            }
                            e.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
                            e.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
                            return true;
                        } else if (helmet.getType() != Material.PLAYER_HEAD) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Dead Miner = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Dead Miner = "))) {
                                e.removeMetadata(config.getString("Dead Miner = "), DangerousCaves.INSTANCE);
                            }
                            e.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
                            e.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
                            return true;
                        }
                    } else if (name.equals(config.getString("Magma Monster = "))) {
                        ItemStack boot = e.getEquipment().getBoots();
                        if (boot == null) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Magma Monster = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Magma Monster = "))) {
                                e.removeMetadata(config.getString("Magma Monster = "), DangerousCaves.INSTANCE);
                            }
                            e.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
                            e.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
                            if (e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                e.removePotionEffect(PotionEffectType.INVISIBILITY);
                            }
                            if (e.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                                e.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                            }
                            return true;
                        } else if (boot.getType() != Material.LEATHER_BOOTS) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Magma Monster = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Magma Monster = "))) {
                                e.removeMetadata(config.getString("Magma Monster = "), DangerousCaves.INSTANCE);
                            }
                            e.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
                            e.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
                            if (e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                e.removePotionEffect(PotionEffectType.INVISIBILITY);
                            }
                            if (e.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                                e.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                            }
                            return true;
                        }
                    } else if (name.equals(config.getString("Smoke Demon = "))) {
                        String cus = e.getCustomName();
                        if (e.getType() != EntityType.ZOMBIE) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Smoke Demon = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Smoke Demon = "))) {
                                e.removeMetadata(config.getString("Smoke Demon = "), DangerousCaves.INSTANCE);
                            }
                            if (e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                e.removePotionEffect(PotionEffectType.INVISIBILITY);
                            }
                            return true;
                        } else if (cus == null) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Smoke Demon = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Smoke Demon = "))) {
                                e.removeMetadata(config.getString("Smoke Demon = "), DangerousCaves.INSTANCE);
                            }
                            if (e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                e.removePotionEffect(PotionEffectType.INVISIBILITY);
                            }
                            return true;
                        } else if (!cus.equals(config.getString("Smoke Demon = "))) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals(config.getString("Smoke Demon = "))) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata(config.getString("Smoke Demon = "))) {
                                e.removeMetadata(config.getString("Smoke Demon = "), DangerousCaves.INSTANCE);
                            }
                            if (e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                e.removePotionEffect(PotionEffectType.INVISIBILITY);
                            }
                            return true;
                        }
                    } else if (name.equals("The Darkness")) {
                        String cus = e.getCustomName();
                        if (e.getType() != EntityType.HUSK) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals("The Darkness")) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata("The Darkness")) {
                                e.removeMetadata("The Darkness", DangerousCaves.INSTANCE);
                            }
                            if (e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                e.removePotionEffect(PotionEffectType.INVISIBILITY);
                            }
                            if (e.hasPotionEffect(PotionEffectType.SLOW)) {
                                e.removePotionEffect(PotionEffectType.SLOW);
                            }
                            if (e.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                e.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            }
                            if (e.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                                e.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            }
                            e.setCollidable(true);
                            return true;
                        } else if (cus == null) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals("The Darkness")) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata("The Darkness")) {
                                e.removeMetadata("The Darkness", DangerousCaves.INSTANCE);
                            }
                            if (e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                e.removePotionEffect(PotionEffectType.INVISIBILITY);
                            }
                            if (e.hasPotionEffect(PotionEffectType.SLOW)) {
                                e.removePotionEffect(PotionEffectType.SLOW);
                            }
                            if (e.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                e.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            }
                            if (e.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                                e.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            }
                            e.setCollidable(true);
                            return true;
                        } else if (!cus.equals("The Darkness")) {
                            e.setSilent(false);
                            if (e.getCustomName() != null) {
                                if (e.getCustomName().equals("The Darkness")) {
                                    e.setCustomName("");
                                }
                            }
                            if (e.hasMetadata("The Darkness")) {
                                e.removeMetadata("The Darkness", DangerousCaves.INSTANCE);
                            }
                            if (e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                e.removePotionEffect(PotionEffectType.INVISIBILITY);
                            }
                            if (e.hasPotionEffect(PotionEffectType.SLOW)) {
                                e.removePotionEffect(PotionEffectType.SLOW);
                            }
                            if (e.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                e.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            }
                            if (e.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                                e.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            }
                            e.setCollidable(true);
                            return true;
                        }
                    }
                    return false;
                } catch (Exception error) {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean existTarget(Entity e) {
        if(e == null) {
            return false;
        }
        return !e.isDead();
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if(!existMonsterBefore(event.getEntity())) {
            return;
        }
        else if(!existTarget(event.getTarget())) {
            return;
        }
        //console.sendMessage("yea");
        if (hungerdark) {
            if (config.getBoolean("Enable Monster Fixing ")) {
                if (!worlds.contains(event.getEntity().getWorld().getName())) {
                    return;
                }
                if (event.getEntity() instanceof Monster) {
                    if (!fixMonster((LivingEntity) event.getEntity())) {
                        Monster e = (Monster) event.getEntity();
                        if (hasName("The Darkness", e)) {
                            if (event.getTarget().getLocation().getBlock().getLightLevel() > 0) {
                                event.setTarget(null);
                            } else {
                                try {
                                    effectEnts.add(event.getEntity());
                                } catch (Exception error) {
                                    console.sendMessage(ChatColor.RED + "Uh oh error inside targeting.");
                                }
                            }
                        }
                    }
                }
            }
            else {
                if (!worlds.contains(event.getEntity().getWorld().getName())) {
                    return;
                }
                if (event.getEntity() instanceof Monster) {
                    if (!fixMonster((LivingEntity) event.getEntity())) {
                        Monster e = (Monster) event.getEntity();
                        if (hasName("The Darkness", e)) {
                            if (event.getTarget().getLocation().getBlock().getLightLevel() > 0) {
                                event.setTarget(null);
                            }
                            else {
                                try {
                                    effectEnts.add(event.getEntity());
                                } catch (Exception error) {
                                    console.sendMessage(ChatColor.RED + "Uh oh error inside targeting.");
                                }
                            }
                        }
                    }
                }
            }
        }
        if (caveents) {
            if (event.getEntity() instanceof Monster) {
                if (config.getBoolean("Enable Monster Fixing ")) {
                    if (!fixMonster((LivingEntity) event.getEntity())) {
                        Monster e = (Monster) event.getEntity();
                        if (hasName(config.getString("Watcher = "), e)) {
                            if (e.getCustomName() != null) {
                                String name = e.getCustomName();
                                if (name.toLowerCase().contains("elite")) {
                                    e.remove();
                                }
                            }
                        }
                        else if (hasName(config.getString("Magma Monster = "), e)) {
                            effectEnts.add(event.getEntity());
                        }
                        else if (hasName(config.getString("Dead Miner = "), e)) {
                            effectEnts.add(event.getEntity());
                        }
                        else if (hasName(config.getString("Lava Creeper = "), e)) {
                            effectEnts.add(event.getEntity());
                        }
                        else if (hasName(config.getString("Smoke Demon = "), e)) {
                            effectEnts.add(event.getEntity());
                        }
                    }
                } else {
                    Monster e = (Monster) event.getEntity();
                    if (hasName(config.getString("Watcher = "), e)) {
                        effectEnts.add(event.getEntity());
                    }
                    else if (hasName(config.getString("Magma Monster = "), e)) {
                        effectEnts.add(event.getEntity());
                    }
                    else if (hasName(config.getString("Dead Miner = "), e)) {
                        effectEnts.add(event.getEntity());
                    }
                    else if (hasName(config.getString("Lava Creeper = "), e)) {
                        effectEnts.add(event.getEntity());
                    }
                    else if (hasName(config.getString("Smoke Demon = "), e)) {
                        effectEnts.add(event.getEntity());
                    }
                }
            }
        }
    }

    private boolean existMonsterBefore(Entity e) {
        if (e == null) {
            return false;
        }
        if (!(e instanceof Monster)) {
            return false;
        }
        Monster m = (Monster) e;
        return !m.isDead();
    }

    private boolean existMonster(Entity e) {
        if (e == null) {
            return false;
        }
        if (!(e instanceof Monster)) {
            return false;
        }
        Monster m = (Monster) e;
        if (m.isDead()) {
            return false;
        }
        if (m.getTarget() == null) {
            return false;
        }
        return !m.getTarget().isDead();
    }

    private boolean existBat(Entity e) {
        if (e == null) {
            return false;
        }
        if (!(e instanceof Bat)) {
            return false;
        }
        return !e.isDead();
    }

    private void betterEffectLooper() {
        if (!effectEnts.isEmpty()) {
            List<Entity> tempEntss = new ArrayList<>(effectEnts);
            for (Entity e : tempEntss) {
                LivingEntity e2 = (LivingEntity) e;
                if (!worlds.contains(e2.getWorld().getName())) {
                    effectEnts.remove(e);
                } else {
                    if (existMonster(e)) {
                        String name = e2.getCustomName();
                        if (name != null) {
                            boolean continuee;
                            if (config.getBoolean("Enable Broken Monster Deletion ")) {
                                if (name.toLowerCase().contains("elite")) {
                                    continuee = false;
                                    effectEnts.remove(e);
                                    e.remove();
                                } else {
                                    continuee = true;
                                }
                            } else {
                                continuee = true;
                            }
                            if (continuee) {
                                if (hasName("The Darkness", e)) {
                                    if ((e.getLocation().getBlock().getLightLevel() > 0) || (e.getFireTicks() > 0)) {
                                        e.remove();
                                    }
                                    else if (((Monster) e).getTarget().hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                                        e.remove();
                                    }
                                    else if ((((Monster) e).getTarget().getLocation().getBlock().getLightLevel() == 0)) {
                                        e.getWorld().playSound(e.getLocation(), Sound.ENTITY_CAT_PURR, (float) .5, 0);
                                    }
                                } else if (hasName(config.getString("Watcher = "), e)) {
                                    LivingEntity e3 = ((Monster) e).getTarget();
                                    if (e3 instanceof Player) {
                                        if (!getLookingAt2(e3, (LivingEntity) e)) {
                                            Player p = (Player) e3;
                                            Location loc = getBlockInFrontOfPlayer(p);
                                            double newYaw;
                                            double newPitch;
                                            if (p.getLocation().getYaw() < 0) {
                                                newYaw = p.getLocation().getYaw() + 180;
                                            } else {
                                                newYaw = p.getLocation().getYaw() - 180;
                                            }
                                            newPitch = p.getLocation().getPitch() * -1;
                                            Location jumpLoc = new Location(p.getWorld(), loc.getX(),
                                                    p.getLocation().getY(), loc.getZ(), ((float) newYaw),
                                                    ((float) newPitch));
                                            e.teleport(jumpLoc);
                                            e.setVelocity(new Vector(0, 0, 0));
                                            p.setVelocity(new Vector(0, 0, 0));
                                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 200));
                                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_HURT, 1, 2);
                                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 2));
                                        }
                                    }
                                } else if (hasName(config.getString("Dead Miner = "), e)) {
                                    if (e.getLocation().getBlock().getLightLevel() == 0) {
                                        e.getLocation().getBlock().setType(Material.TORCH);
                                    }
                                } else if (hasName(config.getString("Magma Monster = "), e)) {
                                    if (randor.nextInt(14) == 1) {
                                        e.getLocation().getBlock().setType(Material.FIRE);
                                    }
                                    if (randor.nextInt(28) == 1) {
                                        e.getLocation().subtract(0, 1, 0).getBlock().setType(Material.MAGMA_BLOCK);
                                    }
                                } else if (hasName(config.getString("Lava Creeper = "), e)) {
                                    if (randor.nextInt(24) == 1) {
                                        e.getWorld().spawnParticle(Particle.LAVA, e.getLocation().add(0, 1, 0), 1);
                                    }
                                } else if (hasName(config.getString("Smoke Demon = "), e)) {
                                    if (e.getLocation().getBlock().getLightLevel() < 12) {
                                        List<Entity> ents = e.getNearbyEntities(3, 3, 3);
                                        for (Entity es : ents) {
                                            if(es == e) {

                                            }
                                            else if (es instanceof LivingEntity) {
                                                ((LivingEntity) es).addPotionEffect(
                                                        new PotionEffect(PotionEffectType.BLINDNESS, 120, 1));
                                                ((LivingEntity) es).addPotionEffect(
                                                        new PotionEffect(PotionEffectType.WITHER, 120, 0));
                                            }
                                        }
                                        e.getWorld().spawnParticle(Particle.CLOUD, e.getLocation().add(0, 1, 0), 60,
                                                1, 1, 1, 0.00f/* , m */);
                                    } else {
                                        e.remove();
                                    }
                                } else {
                                    effectEnts.remove(e);
                                }
                            } else {
                                effectEnts.remove(e);
                            }
                        } else {
                            effectEnts.remove(e);
                        }
                    } else if (existBat(e)) {
                        String name = e2.getCustomName();
                        if (name != null) {
                            if (hasName(config.getString("Crying Bat = "), e)) {
                                if (randor.nextInt(30) == 1) {
                                    e.getWorld().playSound(e.getLocation(), Sound.ENTITY_WOLF_WHINE, 1,
                                            (float) (1.4 + (randor.nextInt(5 + 1) / 10.0)));
                                    if (randor.nextInt(5) == 1) {
                                        ((Bat) e).damage(999999);
                                    }
                                }
                            }
                        } else {
                            effectEnts.remove(e);
                        }
                    } else {
                        effectEnts.remove(e);
                    }
                }
            }
        }
    }

    private static Location getBlockInFrontOfPlayer(LivingEntity entsa) {
        Vector inverseDirectionVec = entsa.getLocation().getDirection().normalize().multiply(1);
        return entsa.getLocation().add(inverseDirectionVec);
    }

    @EventHandler
    public void onMobName(PlayerInteractEntityEvent event) {
        if (!worlds.contains(event.getRightClicked().getWorld().getName())) {
            return;
        }
        if (!(event.getRightClicked() instanceof Player)) {
            ItemStack i = event.getPlayer().getInventory().getItemInMainHand();
            if (i.getType() == Material.NAME_TAG) {
                if (i.hasItemMeta()) {
                    if (i.getItemMeta().hasDisplayName()) {
                        String s = i.getItemMeta().getDisplayName();
                        if (mobNames.contains(s)) {
                            removeItemNaturally(event.getPlayer());
                            if (event.getRightClicked() instanceof LivingEntity
                                    && (!(event.getRightClicked() instanceof Player))) {
                                event.getRightClicked().setCustomName("");
                                event.setCancelled(true);
                            } else {
                                event.getRightClicked().remove();
                            }
                        }
                    }
                }
            }
        }
    }

    private static void removeItemNaturally(Player p) {
        try {
            if (p.getInventory().getItemInMainHand().getAmount() <= 1) {
                // p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                p.getInventory().getItemInMainHand().setAmount(0);
            } else {
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
            }
        } catch (Exception ignored) {

        }
    }

    @EventHandler
    public void deleteLightLevel(CreatureSpawnEvent event) {
        if (!worlds.contains(event.getEntity().getWorld().getName())) {
            return;
        }
        if (hungerdark || caveents) {
            Entity e = event.getEntity();
            try {
                e.getLocation().subtract(0, 1, 0).getBlock();
                boolean isEnemy = (e instanceof Creeper || e instanceof Skeleton || e instanceof Zombie
                        || e instanceof Spider)
                        && (!(e instanceof PigZombie || e instanceof ZombieVillager
                        || e instanceof Husk));
                if (randor.nextInt(config.getInt("Darkness Spawn Chance ") + 1) != 0 || !hungerdark) {
                    if (caveents) {
                        if (!e.isDead()) {
                            if (isEnemy) {
                                if (event.getSpawnReason() == SpawnReason.NATURAL && event.getSpawnReason() != SpawnReason.REINFORCEMENTS && isStony(e.getLocation().subtract(0, 1, 0).getBlock().getType())) {
                                    if (e.getLocation().getY() <= config.getInt("Monster Spawning Highest Y ")
                                            && e.getLocation().getY() >= config.getInt("Monster Spawning Lowest Y ")) {
                                        doMobSpawns(e);
                                    }
                                }
                            }
                        }
                    } else {
                        return;
                    }
                } else {
                    if (!e.isDead()) {
                        if (isEnemy) {
                            if (event.getSpawnReason() == SpawnReason.NATURAL && isStony(e.getLocation().subtract(0, 1, 0).getBlock().getType())) {
                                if (e.getLocation().getY() <= config.getInt("Monster Spawning Highest Y ")
                                        && e.getLocation().getY() >= config.getInt("Monster Spawning Lowest Y ")) {
                                    doDarkness(e);
                                }
                            }
                        }
                    }
                }
            } catch (Exception error) {
                console.sendMessage(ChatColor.RED + "Uh oh error inside spawning method.");
            }
        }
    }

    private boolean exists(Entity e) {
        return e != null;
    }

    private boolean hasName(String n, Entity e) {
        if(exists(e)) {
            if(e.hasMetadata(n)) {
                return true;
            }
            if(e.getCustomName() != null) {
                String name = ChatColor.stripColor(e.getCustomName());
                if((!name.equals("")) && (!name.equals(" "))) {
                    return name.equals(n);
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (hungerdark) {
            if (!worlds.contains(e.getEntity().getWorld().getName())) {
                return;
            }
            if (hasName("The Darkness", e.getEntity())) {
                List<ItemStack> drops = e.getDrops();
                for (ItemStack i : drops) {
                    i.setType(Material.AIR);
                }
            }
        }
    }

    private void doDarkness(Entity entitye) {
        try {
            LivingEntity e = (LivingEntity) entitye;
            if (e != null && !e.isDead()) {
                if (e.getType() != EntityType.HUSK) {
                    Entity e2 = e.getWorld().spawnEntity(e.getLocation(), EntityType.HUSK);
                    e.remove();
                    e = (LivingEntity) e2;
                }
                String name = "The Darkness";
                e.setCustomName(name);
                e.setMetadata(name, new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                e.setMetadata("R", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                e.setMetadata("cavem", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                e.setMetadata("darkness", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                e.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 200, false, false));
                e.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 2, false, false));
                e.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, damage, false, false));
                e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 3, false, false));
                e.setSilent(true);
                e.setCanPickupItems(false);
                e.setCustomNameVisible(false);
                e.setCollidable(false);
            }
        } catch (Exception error) {
            console.sendMessage(ChatColor.RED + "Uh oh error inside darkness dressing.");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (hungerdark) {
            if (!worlds.contains(e.getEntity().getWorld().getName())) {
                return;
            }
            if (hasName("The Darkness", e.getEntity())) {
                if (((LivingEntity) e.getEntity()).getHealth() - e.getFinalDamage() <= 0) {
                    e.setCancelled(true);
                    e.getEntity().remove();
                }
            }
        }
    }

    @EventHandler
    public void onDamageP(EntityDamageByEntityEvent e) {
        if (!worlds.contains(e.getEntity().getWorld().getName())) {
            return;
        }
        if (e.getEntity() instanceof Player) {
            if (hasName("The Darkness", e.getDamager())) {
                if (e.getEntity().getLocation().getBlock().getLightLevel() > 0) {
                    e.setCancelled(true);
                    e.getDamager().remove();
                }
            }
        }
    }

    //

    // Cave Aging

    private boolean inCave(Player p) {
        boolean cave = true;
        if (p.getLocation().getBlock().getLightFromSky() > 0) {
            return false;
        }
        if (p.getLocation().getY() < 49) {
            return false;
        }
        if (p.getGameMode() != GameMode.SURVIVAL) {
            return false;
        }
        if (!isStony(p.getLocation().subtract(0, 1, 0).getBlock().getType())) {
            return false;
        }
        return cave;
    }

    private void doCaveStuff(Player p) {
        if (!worlds.contains(p.getWorld().getName())) {
            return;
        }
        if(inCave(p)) {
            if (randor.nextInt(config.getInt("Cave Aging Chance ") + 1) == 0) {
                World wor = p.getWorld();
                Random rand = new Random();
                Location loc = p.getLocation();
                int radius = 45;
                int cx = loc.getBlockX();
                int cy = loc.getBlockY();
                int cz = loc.getBlockZ();
                for (int x = cx - radius; x <= cx + radius; x++) {
                    for (int z = cz - radius; z <= cz + radius; z++) {
                        for (int y = (cy - radius); y < (cy + radius); y++) {
                            double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));

                            if (dist < radius * radius) {
                                if (rand.nextInt(config.getInt("Cave Aging Change Chance ") + 1) == 0) {
                                    Location l = new Location(loc.getWorld(), x, y, z);
                                    doCaveBlocks(l.getBlock());
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private void doCaveBlocks(Block b) {
        if (b.getLocation().getY() > 50 || b.getLightFromSky() > 0) {
            return;
        }
        if (isStony(b.getType()) && randor.nextInt(2) == 1) {
            int chose = randor.nextInt(3);
            if (chose == 0) {
                b.setType(Material.COBBLESTONE);
            } else if (chose == 1) {
                b.setType(Material.ANDESITE);
            } else {
                Block b2 = b.getLocation().subtract(0, 1, 0).getBlock();
                if (randor.nextInt(2) == 1 && isAir(b2.getType())) {
                    b2.setType(Material.COBBLESTONE_WALL);
                }
            }
        }
        if (b.getType() == Material.COBBLESTONE_WALL) {
            if (randor.nextInt(3) == 1) {
                b.setType(Material.COBBLESTONE);
            } else {
                Block b2 = b.getLocation().subtract(0, 1, 0).getBlock();
                if (randor.nextInt(2) == 1 && isAir(b2.getType())) {
                    b2.setType(Material.COBBLESTONE_WALL);
                }
            }
        }
        if ((isStony(b.getType())) && (randor.nextInt(8) == 1)) {
            doVines2(b.getLocation());
        }
        if ((isStony(b.getType())) && randor.nextInt(9) == 1) {
            Block b2 = b.getRelative(BlockFace.UP);
            if (isAir(b2.getType())) {
                if (randor.nextBoolean()) {
                    b2.setType(Material.BROWN_MUSHROOM);
                } else {
                    b2.setType(Material.RED_MUSHROOM);
                }
            }
        }
        if ((isStony(b.getType())) && randor.nextInt(6) == 1) {
            Block b2 = b.getRelative(BlockFace.UP);
            if (isAir(b2.getType())) {
                b2.setType(Material.STONE_BUTTON);
                Switch dr = (Switch) b2.getBlockData();
                dr.setFace(Face.FLOOR);
                b2.setBlockData(dr);
            }
        }
    }

    private boolean isAir(Material m) {
        return m == Material.AIR || m == Material.CAVE_AIR || m == Material.VOID_AIR;
    }

    private boolean isStony(Material m) {
        return m.name().toLowerCase().contains("dirt") || m == Material.STONE || m == Material.MOSSY_COBBLESTONE
                || m == Material.ANDESITE || m == Material.DIORITE || m == Material.COBBLESTONE || m == Material.GRANITE
                || m == Material.GRAVEL;
    }

    private void doVines2(Location l) {
        World wor = l.getWorld();
        if (wor != null) {
            Block b = l.getBlock();
            if (b.getType().name().toLowerCase().contains("leave") || isStony(b.getType())) {
                BlockFace[] blocksf = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
                for (BlockFace blockface : blocksf) {
                    Block b2 = l.getBlock().getRelative(blockface);
                    if (isAir(b2.getType())) {
                        b2.setType(Material.VINE);
                        if (b2.getBlockData() instanceof MultipleFacing) {
                            MultipleFacing dr = (MultipleFacing) b2.getBlockData();
                            dr.setFace(oppisiteBf(blockface), true);
                            b2.setBlockData(dr);
                        }
                    }
                }
            }
        }
    }

    private BlockFace oppisiteBf(BlockFace be) {
        if (be == BlockFace.EAST) {
            return BlockFace.WEST;
        } else if (be == BlockFace.WEST) {
            return BlockFace.EAST;
        } else if (be == BlockFace.NORTH) {
            return BlockFace.SOUTH;
        } else {
            return BlockFace.NORTH;
        }
    }

    //

    // Cave Temperature

    @EventHandler
    public void onWalk(PlayerMoveEvent event) {
        if (cavetemp) {
            try {
                Player p = event.getPlayer();
                if ((!worlds.contains(p.getWorld().getName()))
                        || (p.getLocation().getBlock().getBiome().toString().contains("OCEAN"))) {
                    return;
                }
                if (randor.nextInt(config.getInt("Cave Walk Temp Chance ") + 1) == 0) {
                    if (p.getLocation().getY() < 30 && (p.getGameMode() != GameMode.CREATIVE)) {
                        if (p.getInventory().contains(Material.POTION)
                                || p.getInventory().contains(Material.SPLASH_POTION)
                                || p.getInventory().contains(Material.SNOW)
                                || p.getInventory().contains(Material.SNOWBALL)
                                || p.getInventory().contains(Material.SNOW_BLOCK)
                                || p.getInventory().contains(Material.WATER_BUCKET)
                                || p.getInventory().contains(Material.ICE)
                                || p.getInventory().contains(Material.FROSTED_ICE)
                                || p.getInventory().contains(Material.PACKED_ICE)) {
                        } else if (p.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                        } else {
                            if (hotmessage.size() != 0) {
                                int msg = randor.nextInt(hotmessage.size());
                                p.sendMessage(ChatColor.RED + hotmessage.get(msg));
                            }
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 1));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 55, 1));
                        }
                    }
                }
            } catch (Exception error) {
                console.sendMessage(ChatColor.RED + "Uh oh error inside moving.");
            }
        }
    }

    //

    // Cave Ins

    @EventHandler
    public void onBreakB(BlockBreakEvent dr) {
        Player p = dr.getPlayer();
        if (!worlds.contains(p.getWorld().getName())) {
            return;
        }
        if (caveins) {
            if (randor.nextInt(config.getInt("Cave-In Chance ") + 1) == 0) {
                if (dr.getPlayer().getLocation().getY() < 25) {
                    if ((isStony(dr.getBlock().getType()))
                            && (isStony(p.getLocation().subtract(0, 1, 0).getBlock().getType()))) {
                        if (!p.getInventory().contains(Material.RABBIT_FOOT)) {
                            Location loc = p.getLocation();
                            int radius = 7;
                            int cx = loc.getBlockX();
                            int cy = loc.getBlockY();
                            int cz = loc.getBlockZ();
                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 65, 3));
                            for (int x = cx - radius; x <= cx + radius; x++) {
                                for (int z = cz - radius; z <= cz + radius; z++) {
                                    for (int y = (cy - radius); y < (cy + radius); y++) {
                                        double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z)
                                                + ((cy - y) * (cy - y));

                                        if (dist < radius * radius) {
                                            Location l = new Location(loc.getWorld(), x, y + 2, z);
                                            Block b = l.getBlock();
                                            if (isAir(b.getType()) || b.getType() == Material.BEDROCK || b.getType()
                                                    .toString().toLowerCase().contains("shulker_box")) {
                                            } else {
                                                b.getWorld().spawnFallingBlock(b.getLocation(), b.getBlockData());
                                                l.getBlock().setType(Material.AIR);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (cavetemp && (!p.getLocation().getBlock().getBiome().toString().contains("OCEAN"))) {
            if (randor.nextInt(config.getInt("Cave Break Block Temp Chance ") + 1) == 0) {
                if (p.getLocation().getY() < 30 && (p.getGameMode() != GameMode.CREATIVE)) {
                    if (p.getInventory().contains(Material.POTION)
                            || p.getInventory().contains(Material.SPLASH_POTION)
                            || p.getInventory().contains(Material.SNOW)
                            || p.getInventory().contains(Material.SNOWBALL)
                            || p.getInventory().contains(Material.SNOW_BLOCK)
                            || p.getInventory().contains(Material.WATER_BUCKET)
                            || p.getInventory().contains(Material.ICE)
                            || p.getInventory().contains(Material.FROSTED_ICE)
                            || p.getInventory().contains(Material.PACKED_ICE)) {
                    } else if (p.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                    } else {
                        if (hotmessage.size() != 0) {
                            int msg = randor.nextInt(hotmessage.size());
                            p.sendMessage(ChatColor.RED + hotmessage.get(msg));
                        }
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 1));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 55, 1));
                    }
                }
            }
        }
    }

    //

    // Cave Mobs

    private String mobTypes() {
        int choice = randor.nextInt(9);
        try {
            if (choice == 0) {
                if (config.getBoolean("Spawn Watcher ")) {
                    return config.getString("Watcher = ");
                } else {
                    return "";
                }
            } else if (choice == 1) {
                if (config.getBoolean("Spawn TnT Creeper ")) {
                    return config.getString("TnT Creeper = ");
                } else {
                    return "";
                }
            } else if (choice == 2) {
                if (config.getBoolean("Spawn Dead Miner ")) {
                    return config.getString("Dead Miner = ");
                } else {
                    return "";
                }
            } else if (choice == 3) {
                if (config.getBoolean("Spawn Lava Creeper ")) {
                    return config.getString("Lava Creeper = ");
                } else {
                    return "";
                }
            } else if (choice == 4) {
                if (config.getBoolean("Spawn Magma Monster ")) {
                    return config.getString("Magma Monster = ");
                } else {
                    return "";
                }
            } else if (choice == 5) {
                if (config.getBoolean("Spawn Smoke Demon ")) {
                    return config.getString("Smoke Demon = ");
                } else {
                    return "";
                }
            } else if (choice == 6) {
                if (config.getBoolean("Spawn Crying Bat ")) {
                    if (randor.nextInt(20) == 1) {
                        return config.getString("Crying Bat = ");
                    } else {
                        return "";
                    }
                } else {
                    return "";
                }
            } else if (choice == 7) {
                if (config.getBoolean("Spawn Alpha Spider ")) {
                    return config.getString("Alpha Spider = ");
                } else {
                    return "";
                }
            } else if (choice == 8) {
                if (config.getBoolean("Spawn Hexed Armor ")) {
                    return config.getString("Hexed Armor = ");
                } else {
                    return "";
                }
            }
            return "";
        } catch (Exception error) {
            console.sendMessage(ChatColor.RED + "Uh oh error inside mob names.");
            return "";
        }
    }

    private void doMobSpawns(Entity entitye) {
        LivingEntity e = (LivingEntity) entitye;
        String name = mobTypes();
        if (e != null && !e.isDead()) {
            try {
                if (name.equals(config.getString("Watcher = "))
                        && (randor.nextInt(config.getInt("Watcher Chance ") + 1) == 0)) {
                    // now teleports in front of the player if the player looks away from it
                    Entity e2 = e.getWorld().spawnEntity(e.getLocation(), EntityType.HUSK);
                    e.remove();
                    e = (LivingEntity) e2;
                    e.setSilent(true);
                    e.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 200));
                    EntityEquipment ee = e.getEquipment();
                    ItemStack myAwesomeSkull = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta myAwesomeSkullMeta = (SkullMeta) myAwesomeSkull.getItemMeta();
                    myAwesomeSkullMeta.setOwner("Creegn");
                    myAwesomeSkull.setItemMeta(myAwesomeSkullMeta);
                    ee.setHelmet(myAwesomeSkull);
                    ee.setHelmetDropChance(0);
                    e.setCustomName(name);
                    e.setMetadata(name, new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setMetadata("R", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setMetadata("cavem", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    removeHand(e);
                    e.setCanPickupItems(false);
                } else if (name.equals(config.getString("TnT Creeper = "))
                        && (randor.nextInt(config.getInt("TnT Creeper Chance ") + 1) == 0)) {
                    if (e.getType() != EntityType.CREEPER) {
                        Entity e2 = e.getWorld().spawnEntity(e.getLocation(), EntityType.CREEPER);
                        e.remove();
                        e = (LivingEntity) e2;
                    }
                    e.setCustomName(name);
                    e.setMetadata(name, new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setMetadata("R", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                } else if (name.equals(config.getString("Lava Creeper = "))
                        && (randor.nextInt(config.getInt("Lava Creeper Chance ") + 1) == 0)) {
                    if (e.getType() != EntityType.CREEPER) {
                        Entity e2 = e.getWorld().spawnEntity(e.getLocation(), EntityType.CREEPER);
                        e.remove();
                        e = (LivingEntity) e2;
                    }
                    e.setCustomName(name);
                    e.setMetadata(name, new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setMetadata("R", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                } else if (name.equals(config.getString("Dead Miner = "))
                        && (randor.nextInt(config.getInt("Dead Miner Chance ") + 1) == 0)) {
                    if (e.getType() != EntityType.ZOMBIE) {
                        Entity e2 = e.getWorld().spawnEntity(e.getLocation(), EntityType.ZOMBIE);
                        e.remove();
                        e = (LivingEntity) e2;
                    }
                    EntityEquipment ee = e.getEquipment();
                    ItemStack myAwesomeSkull = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta myAwesomeSkullMeta = (SkullMeta) myAwesomeSkull.getItemMeta();
                    myAwesomeSkullMeta.setOwner("wallabee");
                    myAwesomeSkull.setItemMeta(myAwesomeSkullMeta);
                    ee.setHelmet(myAwesomeSkull);
                    ee.setHelmetDropChance(0);
                    setMinerHands(e);
                    e.setCustomName(name);
                    e.setMetadata(name, new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setMetadata("R", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setCanPickupItems(false);
                } else if (name.equals(config.getString("Smoke Demon = "))
                        && (randor.nextInt(config.getInt("Smoke Demon Chance ") + 1) == 0)) {
                    if (e.getType() != EntityType.ZOMBIE) {
                        Entity e2 = e.getWorld().spawnEntity(e.getLocation(), EntityType.ZOMBIE);
                        e.remove();
                        e = (LivingEntity) e2;
                    }
                    e
                            .addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, false, false));
                    e.setSilent(true);
                    e.setCustomName(name);
                    e.setMetadata(name, new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setMetadata("R", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setMetadata("cavem", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    removeHand(e);
                    e.setCanPickupItems(false);
                } else if (name.equals(config.getString("Crying Bat = "))
                        && (randor.nextInt(config.getInt("Crying Bat Chance ") + 1) == 0)) {
                    if (e.getType() != EntityType.BAT) {
                        Entity e2 = e.getWorld().spawnEntity(e.getLocation(), EntityType.BAT);
                        e.remove();
                        e = (LivingEntity) e2;
                    }
                    e.setCustomName(name);
                    e.setMetadata(name, new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setMetadata("R", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    effectEnts.add(e);
                } else if (name.equals(config.getString("Alpha Spider = "))
                        && (randor.nextInt(config.getInt("Alpha Spider Chance ") + 1) == 0)) {
                    if (!(e.getType() == EntityType.SPIDER || e.getType() == EntityType.CAVE_SPIDER)) {
                        Entity e2 = e.getWorld().spawnEntity(e.getLocation(), EntityType.SPIDER);
                        e.remove();
                        e = (LivingEntity) e2;
                    }
                    e.setCustomName(name);
                    e.setMetadata(name, new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setMetadata("R", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                } else if (name.equals(config.getString("Hexed Armor = "))
                        && (randor.nextInt(config.getInt("Hexed Armor Chance ") + 1) == 0)) {
                    if (e.getType() != EntityType.ZOMBIE || e.getType() != EntityType.SKELETON) {
                        Entity e2 = e.getWorld().spawnEntity(e.getLocation(), EntityType.ZOMBIE);
                        e.remove();
                        e = (LivingEntity) e2;
                    }
                    e.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, false, false));
                    e.setSilent(true);
                    e.setCustomName(name);
                    e.setMetadata(name, new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setMetadata("R", new FixedMetadataValue(DangerousCaves.INSTANCE, 0));
                    e.setCanPickupItems(false);
                }
            } catch (Exception error) {
                console.sendMessage(ChatColor.RED + "Uh oh error inside dressing mob.");
            }
        }

    }

    private void setMinerHands(Entity e) {
        try {
            EntityEquipment ee = ((LivingEntity) e).getEquipment();
            // 0 == wood 1 == stone 2 == iron
            // 0 == left handed 1 == right handed
            // boolean true = torch in other hand
            int type = randor.nextInt(3);
            boolean holdtorch = randor.nextBoolean();
            int handside = randor.nextInt(2);
            boolean hasboots = randor.nextBoolean();
            boolean haschest = randor.nextBoolean();
            ItemStack pickaxe = null;
            if (type == 0) {
                pickaxe = new ItemStack(Material.WOODEN_PICKAXE);
            } else if (type == 1) {
                pickaxe = new ItemStack(Material.STONE_PICKAXE);
            } else if (type == 2) {
                pickaxe = new ItemStack(Material.IRON_PICKAXE);
            }
            if (handside == 0) {
                ee.setItemInOffHand(pickaxe);
                if (holdtorch) {
                    ee.setItemInMainHand(new ItemStack(Material.TORCH, 1));
                }
            } else {
                ee.setItemInMainHand(pickaxe);
                if (holdtorch) {
                    ee.setItemInOffHand(new ItemStack(Material.TORCH, 1));
                }
            }
            if (hasboots) {
                if (randor.nextBoolean()) {
                    ee.setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
                } else {
                    ee.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
                }
            }
            if (haschest) {
                if (randor.nextBoolean()) {
                    ee.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
                } else {
                    ee.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
                }
            }
        } catch (Exception error) {
            console.sendMessage(ChatColor.RED + "Uh oh error inside mining hands.");
        }
    }

    @EventHandler
    public void entityHit(EntityDamageByEntityEvent event) {
        if (!worlds.contains(event.getEntity().getWorld().getName())) {
            return;
        }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Monster) {
            if (randor.nextInt(6) == 1) {
                if (hasName(config.getString("TnT Creeper = "), event.getEntity())) {
                    event.getDamager().getWorld().createExplosion(event.getDamager().getLocation(), (float) 0.01);
                    return;
                }
            }
            if (randor.nextInt(6) == 1) {
                if (hasName(config.getString("Dead Miner = "), event.getEntity())) {
                    // 0 == cobblestone 1 == dirt 2 == coal 3 == torch
                    int choice = randor.nextInt(4);
                    if (choice == 0) {
                        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
                                new ItemStack(Material.COBBLESTONE, randor.nextInt(3) + 1));
                    } else if (choice == 1) {
                        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
                                new ItemStack(Material.DIRT, randor.nextInt(3) + 1));
                    } else if (choice == 2) {
                        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
                                new ItemStack(Material.COAL, randor.nextInt(2) + 1));
                    } else if (choice == 3) {
                        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
                                new ItemStack(Material.TORCH, randor.nextInt(3) + 1));
                    }
                    return;
                }
            }
            if (hasName(config.getString("Watcher = "), event.getEntity())) {
                event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_SLIME_SQUISH,
                        1, (float) 1.1);
            }
        }
    }

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (!worlds.contains(event.getEntity().getWorld().getName())) {
            return;
        }
        if (event.getEntity().getType() == EntityType.CREEPER) {
            if (hasName(config.getString("TnT Creeper = "), event.getEntity())) {
                Location l = event.getEntity().getLocation();
                Entity e1 = event.getEntity().getWorld().spawnEntity(l, EntityType.PRIMED_TNT);
                e1.setVelocity(new Vector(-1 * (randor.nextInt(5 + 1) / 10.0), randor.nextInt(5 + 1) / 10.0,
                        -1 * (randor.nextInt(5 + 1) / 10.0)));
                if (randor.nextBoolean()) {
                    Entity e2 = event.getEntity().getWorld().spawnEntity(l, EntityType.PRIMED_TNT);
                    e2.setVelocity(new Vector(randor.nextInt(5 + 1) / 10.0, randor.nextInt(5 + 1) / 10.0,
                            randor.nextInt(5 + 1) / 10.0));
                }
                return;
            }
            if (hasName(config.getString("Lava Creeper = "), event.getEntity())) {
                Location l = event.getEntity().getLocation();
                int radius = 4;
                int cx = l.getBlockX();
                int cy = l.getBlockY();
                int cz = l.getBlockZ();
                for (int x = cx - radius; x <= cx + radius; x++) {
                    for (int z = cz - radius; z <= cz + radius; z++) {
                        for (int y = (cy - radius); y < (cy + radius); y++) {
                            double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));

                            if (dist < radius * radius) {
                                Location l2 = new Location(l.getWorld(), x, y, z);
                                if (l2.getBlock().getType() != Material.BEDROCK) {
                                    if (isAir(l2.getBlock().getType())) {
                                        if (randor.nextInt(3) == 1) {
                                            l2.getBlock().setType(Material.FIRE);
                                        }
                                    } else if (!isAir(l2.getBlock().getType())) {
                                        if (randor.nextInt(4) == 1) {
                                            l2.getBlock().setType(Material.MAGMA_BLOCK);
                                        } else if (randor.nextInt(5) == 1) {
                                            l2.getBlock().setType(Material.OBSIDIAN);
                                        } else if (randor.nextInt(6) == 1) {
                                            l2.getBlock().setType(Material.LAVA);
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private void removeHand(Entity e) {
        if (e instanceof LivingEntity) {
            EntityEquipment ee = ((LivingEntity) e).getEquipment();
            ee.setItemInMainHand(new ItemStack(Material.AIR));
            ee.setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    //

}
