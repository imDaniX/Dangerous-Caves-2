package com.github.evillootlye.caves.commands;

import com.github.evillootlye.caves.configuration.Configuration;
import com.github.evillootlye.caves.mobs.MobsManager;
import com.github.evillootlye.caves.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commander implements CommandExecutor {
    private final MobsManager mobsManager;
    private final Configuration cfg;

    public Commander(MobsManager mobsManager, Configuration cfg) {
        this.mobsManager = mobsManager;
        this.cfg = cfg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            sender.sendMessage(Utils.clr("&6&lDangerousCaves by DaddyLootly &e(fork imDaniX)"));
            sender.sendMessage(Utils.clr(" &a/" + label + " info &7- Get some info about your location."));
            sender.sendMessage(Utils.clr(" &a/" + label + " summon [mob] &7- Spawn a mob on your location."));
            sender.sendMessage(Utils.clr(" &a/" + label + " reload &7- Reload plugin configuration."));
        } else switch (args[0].toLowerCase()) {
            case "info":
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Utils.clr("&cYou can't execute this subcommand from console!"));
                    return true;
                } else if(!sender.hasPermission("dangerous.caves.command.info")) return false;
                Location loc = ((Player)sender).getLocation();
                sender.sendMessage(Utils.clr("&bWorld: &f") + loc.getWorld().getName());
                sender.sendMessage(Utils.clr("&bChunk: &f") + loc.getChunk().getX() + "," + loc.getChunk().getZ());
                break;
            case "summon":
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Utils.clr("&cYou can't execute this subcommand from console!"));
                    return true;
                } else if(!sender.hasPermission("dangerous.caves.command.summon")) return false;
                if(args.length < 2) {
                    sender.sendMessage(Utils.clr("&cYou should specify a mob to summon!"));
                    return true;
                }
                if(mobsManager.summon(args[1], ((Player)sender).getLocation())) {
                    sender.sendMessage(Utils.clr("&aMob " + args[1] + " was successfully summoned."));
                } else {
                    sender.sendMessage(Utils.clr("&cThere's no mobs called " + args[1] + "!"));
                }
                break;
            case "reload":
                if(!sender.hasPermission("dangerous.caves.command.reload")) return false;
                cfg.reloadYml();
        }
        return true;
    }
}
