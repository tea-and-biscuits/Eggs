package uk.co.harieo.eggs.players;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;
import uk.co.harieo.minigames.menus.MenuItem;

public class ShopItem extends MenuItem {

	public ShopItem() {
		super(Material.GOLD_INGOT);
		setName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Power-Up Shop");
		setLore(Collections.singletonList(ChatColor.GRAY + "Right Click to Use"));
		setOnClick(player -> player.chat("/shop"));
	}

}
