package com.github.evillootlye.caves;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Deprecated
public class DangerousCavesOld implements Listener, CommandExecutor {

    public static DangerousCavesOld INSTANCE;
    public final FileConfiguration config = getConfig();

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();
    private final Random randor = new Random();
    private static List<String> worlds = new ArrayList<>();
    private final List<Entity> effectEnts = new ArrayList<>();
    private boolean caveents = false;
    private static final List<String> mobNames = new ArrayList<>();
    
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
        caveents = config.getBoolean("Enable Cave Monsters ");
        mobNames.add(config.getString("Smoke Demon = "));
        worlds = config.getStringList("Enabled Worlds - If Left Blank Will Just Use default world ");
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
            if (caveents) {
                betterEffectLooper();
            }
        }, 0L, /* 600 */((long) 3));
    }

    private void createConfigFol() {
        config.addDefault("Spawn Smoke Demon ", true);
        config.addDefault(":::::Monster Names - no Blanks:::::", "");
        config.addDefault("Smoke Demon = ", "Smoke Demon");
        config.addDefault(":::::::::::::::::::::::::::::::::::", "");
        config.addDefault("Smoke Demon Chance ", 0);
        config.addDefault(":::::::::::::::::::::::::::::::::::", "");
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
                return true;
            }
        }
        return true;
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
        if (caveents) {
            if (event.getEntity() instanceof Monster) {
                Monster e = (Monster) event.getEntity();
                if (hasName(config.getString("Smoke Demon = "), e)) {
                    effectEnts.add(event.getEntity());
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
                            if (hasName(config.getString("Smoke Demon = "), e)) {
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
                    }  else {
                        effectEnts.remove(e);
                    }
                }
            }
        }
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

    //

    // Cave Mobs

    private String mobTypes() {
        int choice = randor.nextInt(9);
        try {
            if (choice == 5) {
                if (config.getBoolean("Spawn Smoke Demon ")) {
                    return config.getString("Smoke Demon = ");
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
                if (name.equals(config.getString("Smoke Demon = "))
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
                }
            } catch (Exception error) {
                console.sendMessage(ChatColor.RED + "Uh oh error inside dressing mob.");
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
