package uk.co.harieo.eggs.players;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;
import uk.co.harieo.minigames.MinigamesCore;
import uk.co.harieo.minigames.menus.MenuItem;

public class HubItem extends MenuItem {

	public HubItem() {
		super(Material.RED_BED);
		setName(ChatColor.RED + ChatColor.BOLD.toString() + "Return to Hub");
		setLore(Collections.singletonList(ChatColor.GRAY + "Right Click to Use"));
		setOnClick(MinigamesCore::sendPlayerToFallbackServer);
	}

}
