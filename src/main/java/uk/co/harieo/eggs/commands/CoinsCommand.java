package uk.co.harieo.eggs.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.purchasables.CoinsHandler;

public class CoinsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender.hasPermission("quacktopia.eggs.coins")) {
			if (args.length < 2) {
				sender.sendMessage(Eggs.formatMessage(
						ChatColor.RED + "Insufficient Arguments. Expected: /coins <player> <amount>"));
			} else {
				String targetName = args[0];
				Player target = Bukkit.getPlayer(targetName);
				if (target == null) {
					sender.sendMessage(Eggs.formatMessage(ChatColor.RED + "No player online with name: " + targetName));
				} else {
					String rawAmount = args[1];
					int amount;
					try {
						amount = Integer.parseInt(rawAmount);
					} catch (NumberFormatException ignored) {
						sender.sendMessage(Eggs.formatMessage(ChatColor.RED + "This is not an amount: " + rawAmount));
						return false;
					}

					CoinsHandler.addCoins(target, amount);
					boolean addition = amount > 0;
					sender.sendMessage(Eggs.formatMessage(
							ChatColor.GRAY + "You have " + (addition ? ChatColor.GREEN + "added"
									: ChatColor.RED + "subtracted") + ChatColor.GRAY + " " + amount + " coins " + (addition ? "to " : "from ")
									+ ChatColor.YELLOW + target.getName()));
				}
			}
		} else {
			sender.sendMessage(Eggs.formatMessage(ChatColor.RED + "You do not have permission to do that!"));
		}
		return false;
	}

}
