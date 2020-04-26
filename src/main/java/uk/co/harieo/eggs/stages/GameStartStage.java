package uk.co.harieo.eggs.stages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.List;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.config.GameWorldConfig;
import uk.co.harieo.eggs.scoreboard.CoinsElement;
import uk.co.harieo.eggs.scoreboard.TeamElement;
import uk.co.harieo.eggs.scoreboard.TeamScoreElement;
import uk.co.harieo.eggs.scoreboard.TimeLeftElement;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.minigames.timing.GameTimer;

public class GameStartStage {

	public static final GameTimer gameTimer = new GameTimer(Eggs.getInstance(), Eggs.getInstance().getGameTime());
	private static final GameBoard gameBoard = new GameBoard(ChatColor.GREEN + ChatColor.BOLD.toString() + "Eggs",
			DisplaySlot.SIDEBAR);

	static {
		gameBoard.addBlankLine();
		gameBoard.addLine(new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Time Left"));
		gameBoard.addLine(new TimeLeftElement());
		gameBoard.addBlankLine();
		gameBoard.addLine(new ConstantElement(ChatColor.GOLD + ChatColor.BOLD.toString() + "Your Coins"));
		gameBoard.addLine(new CoinsElement());
		gameBoard.addBlankLine();
		gameBoard.addLine(new TeamElement());
		gameBoard.addLine(new TeamScoreElement());
		gameBoard.addBlankLine();
		gameBoard.addLine(Eggs.IP_ELEMENT);
	}

	public static void startGame() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			EggsTeam assignedTeam;
			if (EggsTeam.isInTeam(player)) {
				assignedTeam = EggsTeam.getTeam(player);
			} else {
				assignedTeam = EggsTeam.assignTeam(player);
				Team team = assignedTeam.getTeam();
				player.sendMessage(Eggs.formatMessage(
						ChatColor.GRAY + "You have been automatically assigned to the " + team.getChatColor() + team
								.getTeamName() + " Team" + ChatColor.GRAY + "!"));
			}

			teleportToSpawn(player, assignedTeam);
			gameBoard.render(Eggs.getInstance(), player, 20);
		});
	}

	public static void renderScoreboard(Player player) {
		gameBoard.render(Eggs.getInstance(), player, 20);
	}

	private static int yellowSpawnIndex = 0;
	private static int orangeSpawnIndex = 0;

	private static void teleportToSpawn(Player player, EggsTeam team) {
		GameWorldConfig worldConfig = Eggs.getInstance().getGameWorldConfig();

		Location location;
		if (team == EggsTeam.ORANGE) {
			List<Location> orangeSpawns = worldConfig.getOrangeSpawns();
			location = orangeSpawns.get(orangeSpawnIndex);
			if (orangeSpawnIndex + 1 < orangeSpawns.size()) { // If there is a following spawn available
				orangeSpawnIndex++;
			} else {
				orangeSpawnIndex = 0;
			}
		} else {
			List<Location> yellowSpawns = worldConfig.getYellowSpawns();
			location = yellowSpawns.get(yellowSpawnIndex);
			if (yellowSpawnIndex + 1 < yellowSpawns.size()) {
				yellowSpawnIndex++;
			} else {
				yellowSpawnIndex = 0;
			}
		}

		player.teleport(location);
	}

}
