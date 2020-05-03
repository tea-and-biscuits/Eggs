package uk.co.harieo.eggs.players;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Egg;

import java.util.Collections;
import uk.co.harieo.eggs.purchasables.handlers.OmeletteHandler;
import uk.co.harieo.minigames.menus.MenuItem;

public class EggCannonItem extends MenuItem {

	public EggCannonItem() {
		super(Material.IRON_SHOVEL);
		setName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Egg Cannon 9000");
		setLore(Collections.singletonList(ChatColor.GRAY + "Right Click to Shoot"));
		setOnClick(player -> {
			player.launchProjectile(Egg.class);
			OmeletteHandler.onRegularEggLaunch(player);
		});
	}

}
