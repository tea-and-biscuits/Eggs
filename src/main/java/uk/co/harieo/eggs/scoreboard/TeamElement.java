package uk.co.harieo.eggs.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;
import uk.co.harieo.minigames.teams.Team;

public class TeamElement implements RenderableElement {

	private final EggsTeam team;

	public TeamElement(EggsTeam team) {
		this.team = team;
	}

	@Override
	public String getText(Player player) {
		EggsTeam playerTeam = EggsTeam.getTeam(player);

		if (playerTeam != null && playerTeam == team) {
			Team teamData = team.getTeam();
			return teamData.getChatColor() + ChatColor.BOLD.toString() + "Your Team";
		} else if (team != null) {
			Team teamData = team.getTeam();
			return teamData.getChatColor() + ChatColor.BOLD.toString() + teamData.getTeamName() + " Team";
		} else {
			return "No Team";
		}
	}

}
