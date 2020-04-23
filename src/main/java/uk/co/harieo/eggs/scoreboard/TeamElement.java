package uk.co.harieo.eggs.scoreboard;

import org.bukkit.entity.Player;

import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;
import uk.co.harieo.minigames.teams.Team;

public class TeamElement implements RenderableElement {

	@Override
	public String getText(Player player) {
		EggsTeam team = EggsTeam.getTeam(player);
		if (team != null) {
			Team teamData = team.getTeam();
			return teamData.getChatColor() + teamData.getTeamName().toUpperCase();
		} else {
			return "No Team";
		}
	}

}
