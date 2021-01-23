package me.imdanix.caves.commands;

import me.imdanix.caves.DangerousCaves;
import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.ticks.Dynamics;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

// TODO: Refactor, maybe split to different classes
public class Commander implements CommandExecutor, TabCompleter {
    private static final List<String> ARGS = Arrays.asList("info", "summon", "spawn", "kill", "tick", "reload", "r");
    private static final Set<String> WITH_MOBS = new HashSet<>(Arrays.asList("summon", "spawn", "kill"));

    private final MobsManager mobsManager;
    private final Configuration cfg;
    private final Dynamics dynamics;

    private final String[] version;

    public Commander(DangerousCaves plugin) {
        this.mobsManager = plugin.getMobs();
        this.cfg = plugin.getConfiguration();
        this.dynamics = plugin.getDynamics();

        String version = plugin.getDescription().getVersion();
        this.version = version.split(";");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            help(sender, label);
        } else switch (args[0].toLowerCase(Locale.ENGLISH)) {
            case "info": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.clr("&cYou can't execute this subcommand from console!"));
                    return true;
                } else if (!sender.hasPermission("dangerous.caves.command.info")) return false;
                Player player = (Player)sender;
                Location loc = player.getLocation();
                sender.sendMessage(Utils.clr("&bWorld: &f") + loc.getWorld().getName());
                sender.sendMessage(Utils.clr("&bChunk: &f") + loc.getChunk().getX() + "," + loc.getChunk().getZ());
                sender.sendMessage(Utils.clr("&bHand: &f") + player.getInventory().getItemInMainHand().getType());
                break;
            }

            // TODO: "debug"

            case "summon": case "spawn": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.clr("&cYou can't execute this subcommand from console!"));
                    return true;
                } else if (!sender.hasPermission("dangerous.caves.command.summon")) return false;
                if (args.length < 2) {
                    sender.sendMessage(Utils.clr("&cYou should specify mob's type to summon!"));
                    return true;
                }
                String type = args[1].toLowerCase(Locale.ENGLISH);
                if (mobsManager.spawn(type, ((Player) sender).getLocation()) != null) {
                    sender.sendMessage(Utils.clr("&aMob &e" + type + "&a was successfully summoned."));
                } else {
                    sender.sendMessage(Utils.clr("&cThere's no mob's type called &e" + type + "&c!"));
                    sender.sendMessage(Utils.clr("&7List of all mob's types: &f" + String.join(", ", mobsManager.getMobs())));
                }
                break;
            }

            case "kill": {
                if (!sender.hasPermission("dangerous.caves.command.kill")) return false;
                if (args.length < 2) {
                    killAll(LivingEntity::remove);
                    sender.sendMessage(Utils.clr("&aSuccessfully killed all DC mobs."));
                } else {
                    String type = args[1].toLowerCase(Locale.ENGLISH);
                    if (mobsManager.getMob(type) == null) {
                        killAll(entity -> {if (Compatibility.isTagged(entity, type)) entity.remove();});
                        sender.sendMessage(Utils.clr("&aSuccessfully killed all DC mobs of type &e" + type + "&a."));
                    } else {
                        sender.sendMessage(Utils.clr("&cThere's no mob's type called &e" + type + "&c!"));
                        sender.sendMessage(Utils.clr("&7List of all mob's types: &f" + String.join(", ", mobsManager.getMobs())));
                    }
                }
                break;
            }

            case "tick": {
                if (!sender.hasPermission("dangerous.caves.command.tick")) return false;
                for (TickLevel level : TickLevel.values()) dynamics.tick(level);
                sender.sendMessage(Utils.clr("&aTicked every tickables."));
                break;
            }

            case "reload": case "r": {
                if (!sender.hasPermission("dangerous.caves.command.reload")) return false;
                cfg.reloadYml();
                sender.sendMessage(Utils.clr("&aPlugin was successfully reloaded."));
                cfg.checkVersion(true);
                break;
            }

            default: help(sender, label);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], ARGS, new ArrayList<>());
        }
        return args.length == 2 && WITH_MOBS.contains(args[0].toLowerCase(Locale.ENGLISH)) ?
               StringUtil.copyPartialMatches(args[1], mobsManager.getMobs(), new ArrayList<>()) : null;
    }

    private static void killAll(Consumer<LivingEntity> kill) {
        for (World world : Bukkit.getWorlds()) for (Entity entity : world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity) entity;
            if (Compatibility.isTagged(living))
                kill.accept(living);
        }
    }

    private void help(CommandSender sender, String label) {
        sender.sendMessage(Utils.clr("&6&lDangerousCaves&e v" + version[0] + " c" + version[1]));
        sender.sendMessage(Utils.clr("&a /" + label + " info &7- Get some info about your location."));
        sender.sendMessage(Utils.clr("&a /" + label + " summon <mob> &7- Spawn a &emob&7 on your location."));
        sender.sendMessage(Utils.clr("&a /" + label + " kill [mob] &7- Kill all DC mobs (of type &emob&7 if specified&7)."));
        sender.sendMessage(Utils.clr("&a /" + label + " tick &7- Tick everything manually."));
        sender.sendMessage(Utils.clr("&a /" + label + " reload &7- Reload plugin configuration."));
    }
}
