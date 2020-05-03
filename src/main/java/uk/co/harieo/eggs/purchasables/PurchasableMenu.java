package uk.co.harieo.eggs.purchasables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.minigames.menus.MenuFactory;
import uk.co.harieo.minigames.menus.MenuItem;

public class PurchasableMenu extends MenuFactory {

	private static final PurchasableMenu instance = new PurchasableMenu();

	private PurchasableMenu() {
		super("Game Shop", 1);
		registerDefaultInteractionListener(Eggs.getInstance());
	}

	@Override
	public void setPlayerItems(Player player, int page) {
		Purchasable[] purchasables = Purchasable.values();
		int purchasableIndex = 0;
		for (int i = 1; i < 9 && purchasableIndex < purchasables.length; i += 2) {
			Purchasable purchasable = purchasables[purchasableIndex];
			boolean canAfford = CoinsHandler.getCoins(player) >= purchasable.getCost();

			MenuItem menuItem = new MenuItem(purchasable.getDisplayMaterial());
			menuItem.setName(
					(canAfford ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD.toString() + purchasable.getName());
			menuItem.setLore(Arrays.asList(ChatColor.GRAY + purchasable.getDescription(),
					ChatColor.YELLOW.toString() + purchasable.getCost() + " Coins",
					"",
					canAfford ? ChatColor.GREEN + "Click to Buy" : ChatColor.RED + "Not Enough Coins"));
			menuItem.setOnClick(clicker -> onClick(purchasable, clicker));
			setItem(player, i, menuItem);
			purchasableIndex++;
		}
	}

	private void onClick(Purchasable purchasable, Player player) {
		int coins = CoinsHandler.getCoins(player);
		int cost = purchasable.getCost();
		if (coins >= cost) {
			CoinsHandler.subtractCoins(player, cost);
			purchasable.activate(player);
			player.getOpenInventory().close();
			Bukkit.broadcastMessage(Eggs.formatMessage(
					player.getDisplayName() + ChatColor.GRAY + " has activated " + ChatColor.GREEN + purchasable
							.getName()));
		} else {
			player.sendMessage(Eggs.formatMessage(
					ChatColor.GRAY + "Earn coins to buy " + ChatColor.GREEN + "power-ups " + ChatColor.GRAY
							+ "by getting kills!"));
		}
	}

	public static PurchasableMenu getInstance() {
		return instance;
	}

}
