package lt.bukkit.accountguard;

import java.io.File;
import java.io.IOException;

import lt.bukkit.accountguard.Main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;

public class AccountGuardCommand implements CommandExecutor {

	Main plugin;
	public String noperm;

	public AccountGuardCommand(Main plugin, String noperm) {
		this.plugin = plugin;
		this.noperm = noperm;

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length == 0) {
			sender.sendMessage("\u00a72----------= \u00a7cAccountGuard " + plugin.getDescription().getVersion() + " by iBo3oF \u00a72=----------");
			sender.sendMessage("\u00a7b/ag \u00a7eDisplay whole commands");
			sender.sendMessage("\u00a7b/ag add \u00a73<account> <ip> \u00a7eActivates the protection and prevents from logging in if IP doesn't match to given");
			sender.sendMessage("\u00a7b/ag remove \u00a73<account> <ip> \u00a7eRemoves the protection");
			sender.sendMessage("\u00a7b/ag info \u00a73<account> \u00a7eChecks if player has protection");
			sender.sendMessage("\u00a7b/ag save \u00a7eSaves the ip file to disk");
			sender.sendMessage("\u00a7b/ag reload \u00a7eReloads the config,translation,ip files from disk");
		} else if (args.length == 3 && args[0].equalsIgnoreCase("add") && checkPermission(sender, "add")) {
			args[1] = args[1].toLowerCase();
			if (!plugin.ip.contains(args[1])) {
				plugin.ip.set(args[1], args[2] + "|");
			} else {
				plugin.ip.set(args[1], plugin.ip.getString(args[1]) + args[2] + "|");
			}
			sender.sendMessage("\u00a72You succesfully protected this account.");
		} else if (args.length == 3 && args[0].equalsIgnoreCase("remove") && checkPermission(sender, "remove")) {
			args[1] = args[1].toLowerCase();
			if (plugin.ip.getString(args[1]) == null) {
				sender.sendMessage("\u00a7cThis player does not have protection!");
			} else if (args.length == 3) {
				if (args[2].equals("*")) {
					plugin.ip.set(args[1], null);
					sender.sendMessage("\u00a7cSuccesfully removed the protection.");
				} else if (!plugin.ip.getString(args[1]).contains(args[2] + "|")) {
					sender.sendMessage("\u00a7cThis ip, which you are trying to remove does not exist.");
				} else {
					if(plugin.ip.getString(args[1]).replaceAll(args[2] + "\\|", "") != "") plugin.ip.set(args[1], plugin.ip.getString(args[1]).replaceAll(args[2] + "\\|", ""));
					else plugin.ip.set(args[1], null);
					sender.sendMessage("\u00a7cSuccesfully removed the protection.");
				}

			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("info") && checkPermission(sender, "info")) {
			if (!plugin.ip.isSet(args[1].toLowerCase())) sender.sendMessage("\u00a7cThis player does not have protection!");
			else {
				sender.sendMessage("\u00a72This account is protected, by these IPS:\u00a7a " + plugin.ip.getString(args[1].toLowerCase()).replaceAll("\\|", ", ").replaceFirst(".$", "").replaceFirst(".$", ""));
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
			if (!plugin.ip.isSet(sender.getName().toLowerCase())) sender.sendMessage("\u00a7cYou don't have protection.");
			else {
				sender.sendMessage("\u00a72Your account is protected, by these IPS:\u00a7a " + plugin.ip.getString(sender.getName().toLowerCase()).replaceAll("\\|", ", ").replaceFirst(".$", "").replaceFirst(".$", ""));
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("save") && checkPermission(sender, "save")) {
			try {
				plugin.ip.save(plugin.ipFile);
			} catch (IOException e) {
				sender.sendMessage("\u00a7cError while saving ip file, please contact the iBo3oF for more help.");
			}
			sender.sendMessage("\u00a72You succesfully saved ip.yml file to disk.");
		} else if (args.length == 1 && args[0].equalsIgnoreCase("reload") && checkPermission(sender, "reload")) {
			plugin.createDefaultFiles();
			plugin.reloadConfig();
			plugin.ipFile = new File(plugin.getDataFolder(), "ip.yml");
			plugin.ip = YamlConfiguration.loadConfiguration(plugin.ipFile);
			HandlerList.unregisterAll(plugin);
			plugin.registerListener();
			sender.sendMessage("\u00a72Succesfully reloaded the plugin.");
		} else {
			sender.sendMessage("\u00a7cBad syntax: \u00a7b/ag \u00a7efor more commands");
		}

		return true;
	}

	public boolean checkPermission(CommandSender sender, String permission) {
		if (sender.hasPermission("accountguard." + permission)) return true;
		else {
			sender.sendMessage(noperm);
			return false;
		}

	}
}
