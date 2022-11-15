package me.imdanix.caves.commands;

import me.imdanix.caves.DangerousCaves;
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.ticks.Dynamics;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.util.TagHelper;
import me.imdanix.caves.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

// TODO: Refactor, split to different classes
public class Commander implements CommandExecutor, TabCompleter {
    private static final List<String> ARGS = Arrays.asList("info", "summon", "spawn", "kill", "tick", "reload", "r", "mimeremove");
    private static final Set<String> WITH_MOBS = new HashSet<>(Arrays.asList("summon", "spawn", "kill"));

    private final MobsManager mobsManager;
    private final Configuration cfg;
    private final Dynamics dynamics;

    private final String[] version;

    public Commander(DangerousCaves plugin) {
        this.mobsManager = plugin.getMobs();
        this.cfg = plugin.getConfiguration();
        this.dynamics = plugin.getDynamics();
        this.version = new String[]{plugin.getDescription().getVersion(), plugin.getConfiguration().getVersion()};
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            help(sender, label);
        } else switch (args[0].toLowerCase(Locale.ROOT)) {
            // TODO: "debug"

            case "summon", "spawn" -> {
                if (!sender.hasPermission("dangerouscaves.command.summon")) return false;
                if (args.length < 2) {
                    sender.sendMessage(Utils.clr("&cYou should specify mob's type to summon!"));
                    return true;
                }
                Location loc = getLocation(sender, Arrays.copyOfRange(args, 2, args.length));
                if (loc == null) {
                    sender.sendMessage(Utils.clr("&cPlease, follow the syntax!"));
                    return true;
                }
                String type = args[1].toLowerCase(Locale.ROOT);
                if (mobsManager.spawn(type, loc) != null) {
                    sender.sendMessage(Utils.clr("&aMob &e" + type + "&a was successfully summoned."));
                } else {
                    sender.sendMessage(Utils.clr("&cThere's no mob's type called &e" + type + "&c!"));
                    sender.sendMessage(Utils.clr("&7List of all mob's types: &f" + String.join(", ", mobsManager.getMobs())));
                }
            }

            case "kill" -> {
                if (!sender.hasPermission("dangerouscaves.command.kill")) return false;
                if (args.length < 2) {
                    killAll(LivingEntity::remove);
                    sender.sendMessage(Utils.clr("&aSuccessfully killed all DC mobs."));
                } else {
                    String type = args[1].toLowerCase(Locale.ROOT);
                    if (mobsManager.getMob(type) == null) {
                        killAll(entity -> {if (TagHelper.isTagged(entity, type)) entity.remove();});
                        sender.sendMessage(Utils.clr("&aSuccessfully killed all DC mobs of type &e" + type + "&a."));
                    } else {
                        sender.sendMessage(Utils.clr("&cThere's no mob's type called &e" + type + "&c!"));
                        sender.sendMessage(Utils.clr("&7List of all mob's types: &f" + String.join(", ", mobsManager.getMobs())));
                    }
                }
            }

            // TODO v Temporary command - need to finish mimics...
            case "mimeremove" -> { // /dcaves mimeremove 3 me
                if (!sender.hasPermission("dangerouscaves.command.kill")) return false;
                boolean checkAll = args.length < 3 || !args[2].equalsIgnoreCase("all") || sender instanceof ConsoleCommandSender;
                int radius = args.length < 2 ? 1 : (int) Utils.getDouble(args[1], 1);

                Collection<? extends Player> players = checkAll ? Bukkit.getOnlinePlayers() : Collections.singleton((Player) sender);
                Set<Integer> checked = new HashSet<>(); // Better to use fastutils, but unavailable in Spigot
                List<BlockState> toRemove = new ArrayList<>();
                for (Player player : players) {
                    World world = player.getWorld();
                    final int centerX = (int) player.getLocation().getX() / 16;
                    final int centerZ = (int) player.getLocation().getZ() / 16;
                    for (int chunkX = centerX - radius; chunkX <= centerX + radius; ++chunkX) for (int chunkZ = centerZ - radius; chunkZ <= centerZ + radius; ++chunkZ) {
                        int hash = chunkZ * (chunkX >> 16) * world.getUID().hashCode();
                        if (!world.isChunkLoaded(chunkX, chunkZ) || checked.contains(hash)) continue;
                        Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                        for (int x = 0; x < 16; ++x) for (int z = 0; z < 16; ++z) {
                            for (BlockState state : chunk.getTileEntities()) {
                                if (state.getType() != Material.CHEST) continue;
                                String tag = TagHelper.getTag(state);
                                if (tag != null && tag.startsWith("mimic")) toRemove.add(state);
                            }
                        }
                        checked.add(hash);
                    }
                }
                sender.sendMessage(Utils.clr("&eRemoving " + toRemove.size() + " mimic chests..."));
                for (BlockState block : toRemove) block.getBlock().setType(Material.AIR, false);
            }
            // TODO ^

            case "tick" -> {
                if (!sender.hasPermission("dangerouscaves.command.tick")) return false;
                for (TickLevel level : TickLevel.values()) dynamics.tick(level);
                sender.sendMessage(Utils.clr("&aTicked every tickables."));
            }

            case "reload", "r" -> {
                if (!sender.hasPermission("dangerouscaves.command.reload")) return false;
                cfg.reloadYml();
                sender.sendMessage(Utils.clr("&aPlugin was successfully reloaded."));
                cfg.checkVersion(true);
            }
            default -> help(sender, label);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], ARGS, new ArrayList<>());
        }
        return args.length == 2 && WITH_MOBS.contains(args[0].toLowerCase(Locale.ROOT)) ?
               StringUtil.copyPartialMatches(args[1], mobsManager.getMobs(), new ArrayList<>()) : null;
    }

    private static Location getLocation(CommandSender sender, String[] args) {
        if (args.length == 0) {                                                 // empty
            if (sender instanceof Entity entitySender) {
                return entitySender.getLocation();
            } else if (sender instanceof BlockCommandSender blockSender) {
                return blockSender.getBlock().getLocation();
            } else {
                return null;
            }
        }

        World world;
        if (args.length == 3 || args.length == 5) {                             // x y z OR x y z yaw pitch
            if (sender instanceof Entity entitySender) {
                world = entitySender.getWorld();
            } else if (sender instanceof BlockCommandSender blockSender) {
                world = blockSender.getBlock().getWorld();
            } else {
                world = Bukkit.getWorlds().get(0);
            }
        } else if (args.length == 4 || args.length == 6) {                      // x y z world OR x y z yaw pitch world
            world = Bukkit.getWorld(args.length == 4 ? args[3] : args[5]);
            if (world == null) return null;
        } else {                                                                // what
            return null;
        }

        // TODO Check if double
        if (args.length < 5) {
            return new Location(
                    world,
                    Utils.getDouble(args[0], 0),
                    Utils.getDouble(args[1], 0),
                    Utils.getDouble(args[2], 0)
            );
        } else {
            return new Location(
                    world,
                    Utils.getDouble(args[0], 0),
                    Utils.getDouble(args[1], 0),
                    Utils.getDouble(args[2], 0),
                    (float) Utils.getDouble(args[3], 0),
                    (float) Utils.getDouble(args[4], 0)
            );
        }
    }

    private void killAll(Consumer<LivingEntity> kill) {
        for (World world : Bukkit.getWorlds()) for (Entity entity : world.getEntities()) {
            if (entity instanceof LivingEntity living && TagHelper.isTagged(living)) kill.accept(living);
        }
    }

    private void help(CommandSender sender, String label) {
        sender.sendMessage(Utils.clr("&6&lDangerousCaves&e v" + version[0] + " c" + version[1]));
        sender.sendMessage(Utils.clr("&a /" + label + " info &7- Get some info about your location."));
        sender.sendMessage(Utils.clr("&a /" + label + " summon <mob> [x] [y] [z] [yaw] [pitch] [world] &7- Spawn a &emob&7 on your or desired location."));
        sender.sendMessage(Utils.clr("&a /" + label + " kill [mob] &7- Kill all DC mobs (of type &emob&7 if specified)."));
        sender.sendMessage(Utils.clr("&a /" + label + " tick &7- Tick everything manually."));
        sender.sendMessage(Utils.clr("&a /" + label + " reload &7- Reload plugin configuration."));
    }
}
