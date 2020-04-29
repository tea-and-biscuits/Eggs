package uk.co.harieo.eggs.scoreboard;

import org.bukkit.entity.Player;

import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;

public class TeamScoreElement implements RenderableElement {

	private final EggsTeam team;

	public TeamScoreElement(EggsTeam team) {
		this.team = team;
	}

	@Override
	public String getText(Player player) {
		int score = 0;
		if (team != null) {
			score = team.getScore();
		}
		return score + " " + (score == 1 ? "Point" : "Points");
	}

}
