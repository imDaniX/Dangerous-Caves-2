/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.caves.commands;

import me.imdanix.caves.DangerousCaves;
import me.imdanix.caves.configuration.Configuration;
import me.imdanix.caves.mobs.MobsManager;
import me.imdanix.caves.ticks.Dynamics;
import me.imdanix.caves.ticks.TickLevel;
import me.imdanix.caves.util.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

// TODO: TabCompleter
public class Commander implements CommandExecutor {
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
        if(args.length < 1) {
            help(sender, label);
        } else switch (args[0].toLowerCase(Locale.ENGLISH)) {
            case "info":
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Utils.clr("&cYou can't execute this subcommand from console!"));
                    return true;
                } else if(!sender.hasPermission("dangerous.caves.command.info")) return false;
                Player player = (Player)sender;
                Location loc = player.getLocation();
                sender.sendMessage(Utils.clr("&bWorld: &f") + loc.getWorld().getName());
                sender.sendMessage(Utils.clr("&bChunk: &f") + loc.getChunk().getX() + "," + loc.getChunk().getZ());
                sender.sendMessage(Utils.clr("&bHand: &f") + player.getInventory().getItemInMainHand().getType());
                break;

            // TODO: "debug"

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

            case "tick":
                if(!sender.hasPermission("dangerous.caves.command.tick")) return false;
                for(TickLevel level : TickLevel.values()) dynamics.tick(level);
                sender.sendMessage(Utils.clr("&aTicked every tickables."));
                break;

            case "reload":
                if(!sender.hasPermission("dangerous.caves.command.reload")) return false;
                cfg.reloadYml();
                sender.sendMessage(Utils.clr("&aPlugin was successfully reloaded."));
                cfg.checkVersion();
                break;

            case "count":
                if(!sender.hasPermission("dangerous.caves.command.count")) return false;
                sender.sendMessage(Utils.clr("&aPlugin ticking &6" + MobsManager.handledCount() + " mobs."));
                break;
            default: help(sender, label);
        }
        return true;
    }

    private void help(CommandSender sender, String label) {
        sender.sendMessage(Utils.clr("&6&lDangerousCaves&e v" + version[0] + " c" + version[1]));
        sender.sendMessage(Utils.clr("&a /" + label + " info &7- Get some info about your location."));
        sender.sendMessage(Utils.clr("&a /" + label + " summon [mob] &7- Spawn a mob on your location."));
        sender.sendMessage(Utils.clr("&a /" + label + " count &7- Count of handled ticking mobs."));
        sender.sendMessage(Utils.clr("&a /" + label + " tick &7- Tick everything manually."));
        sender.sendMessage(Utils.clr("&a /" + label + " reload &7- Reload plugin configuration."));
    }
}
