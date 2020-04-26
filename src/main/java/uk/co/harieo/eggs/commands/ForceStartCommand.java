package uk.co.harieo.eggs.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.stages.GameStartStage;
import uk.co.harieo.minigames.games.GameStage;

public class ForceStartCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!sender.hasPermission("quacktopia.minigames.force")) {
			sender.sendMessage(Eggs.formatMessage(ChatColor.RED + "You do not have permission to do that!"));
			return false;
		} else if (Eggs.getInstance().getGameStage() != GameStage.LOBBY) {
			sender.sendMessage(Eggs.formatMessage(ChatColor.RED + "You may only do this in the lobby stage!"));
			return false;
		}

		int seconds = 0;
		if (args.length > 0) {
			try {
				seconds = Integer.parseInt(args[0]);
			} catch (NumberFormatException ignored) {
			}
		}

		if (seconds == 0) {
			GameStartStage.startGame();
			sender.sendMessage(
					Eggs.formatMessage(ChatColor.GRAY + "The game has been " + ChatColor.RED + "forcefully started."));
		} else {
			Eggs.getInstance().getLobbyTimer().forceTime(seconds);
			sender.sendMessage(Eggs.formatMessage(
					ChatColor.GRAY + "You have set the lobby timer to " + ChatColor.YELLOW + seconds + " seconds. "
							+ ChatColor.RED + "This action is irreversible!"));
		}

		return false;
	}

}
