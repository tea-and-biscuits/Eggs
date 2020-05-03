package uk.co.harieo.eggs.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.purchasables.PurchasableMenu;
import uk.co.harieo.minigames.games.GameStage;

public class ShopCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (Eggs.getInstance().getGameStage() == GameStage.IN_GAME) {
				PurchasableMenu.getInstance().getOrCreateMenu(player).showInventory();
			} else {
				player.sendMessage(Eggs.formatMessage(ChatColor.RED + "You can only do this when the game starts!"));
			}
		} else {
			sender.sendMessage("You must be a player to do that!");
		}
		return false;
	}

}
