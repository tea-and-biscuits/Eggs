package uk.co.harieo.eggs.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;

public class PlayerCountElement implements RenderableElement {

	@Override
	public String getText(Player player) {
		return String.valueOf(Bukkit.getOnlinePlayers().size());
	}

}
