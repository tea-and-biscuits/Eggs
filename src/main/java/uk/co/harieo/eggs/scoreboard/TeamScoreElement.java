package uk.co.harieo.eggs.scoreboard;

import org.bukkit.entity.Player;

import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;

public class TeamScoreElement implements RenderableElement {

	@Override
	public String getText(Player player) {
		EggsTeam team = EggsTeam.getTeam(player);

		int score = 0;
		if (team != null) {
			score = team.getScore();
		}
		return score + " Points";
	}

}
