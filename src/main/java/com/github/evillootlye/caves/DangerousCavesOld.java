package com.github.evillootlye.caves;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
        mobNames.add(config.getString("Lava Creeper = "));
        mobNames.add(config.getString("TnT Creeper = "));
        mobNames.add(config.getString("Watcher = "));
        mobNames.add(config.getString("Smoke Demon = "));
        mobNames.add(config.getString("Dead Miner = "));
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

    private static boolean getLookingAt2(LivingEntity player, LivingEntity player1) {
        Location eye = player.getEyeLocation();
        Vector toEntity = player1.getEyeLocation().toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());

        return dot > 0.70D;
    }

    private void createConfigFol() {
        config.addDefault("Spawn Lava Creeper ", true);
        config.addDefault("Spawn TnT Creeper ", true);
        config.addDefault("Spawn Watcher ", true);
        config.addDefault("Spawn Smoke Demon ", true);
        config.addDefault("Spawn Dead Miner ", true);
        config.addDefault(":::::Monster Names - no Blanks:::::", "");
        config.addDefault("Lava Creeper = ", "Lava Creeper");
        config.addDefault("TnT Creeper = ", "TnT Infused Creeper");
        config.addDefault("Watcher = ", "Watcher");
        config.addDefault("Smoke Demon = ", "Smoke Demon");
        config.addDefault("Dead Miner = ", "Dead Miner");
        config.addDefault(":::::::::::::::::::::::::::::::::::", "");
        config.addDefault("Lava Creeper Chance ", 0);
        config.addDefault("TnT Creeper Chance ", 0);
        config.addDefault("Watcher Chance ", 0);
        config.addDefault("Smoke Demon Chance ", 0);
        config.addDefault("Dead Miner Chance ", 0);
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

    // The Darkness

    private String isMetad(LivingEntity e) {
        if (e.hasMetadata(config.getString("Watcher = "))) {
            return config.getString("Watcher = ");
        } else if (e.hasMetadata(config.getString("Lava Creeper = "))) {
            return config.getString("Lava Creeper = ");
        } else if (e.hasMetadata(config.getString("TnT Creeper = "))) {
            return config.getString("TnT Creeper = ");
        } else if (e.hasMetadata(config.getString("Smoke Demon = "))) {
            return config.getString("Smoke Demon = ");
        } else if (e.hasMetadata(config.getString("Dead Miner = "))) {
            return config.getString("Dead Miner = ");
        }
        return "null";
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
                            if (hasName(config.getString("Watcher = "), e)) {
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
                    }  else {
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

    private boolean isAir(Material m) {
        return m == Material.AIR || m == Material.CAVE_AIR || m == Material.VOID_AIR;
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
            } else if (choice == 5) {
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
