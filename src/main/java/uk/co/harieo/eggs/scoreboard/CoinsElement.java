package uk.co.harieo.eggs.scoreboard;

import org.bukkit.entity.Player;

import uk.co.harieo.eggs.players.CoinsHandler;
import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;

public class CoinsElement implements RenderableElement {

	@Override
	public String getText(Player player) {
		return String.valueOf(CoinsHandler.getCoins(player));
	}

}
