package uk.co.harieo.eggs.stages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.List;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.config.GameWorldConfig;
import uk.co.harieo.eggs.listeners.HotbarHandler;
import uk.co.harieo.eggs.players.EggCannonItem;
import uk.co.harieo.eggs.players.ShopItem;
import uk.co.harieo.eggs.scoreboard.CoinsElement;
import uk.co.harieo.eggs.scoreboard.TeamElement;
import uk.co.harieo.eggs.scoreboard.TeamScoreElement;
import uk.co.harieo.eggs.scoreboard.TimeLeftElement;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.eggs.teams.TabTeamProcessor;
import uk.co.harieo.minigames.MinigamesCore;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.minigames.timing.GameTimer;
import uk.co.harieo.minigames.timing.Timer;

public class GameStartStage {

	public static final GameTimer gameTimer = new GameTimer(Eggs.getInstance(), Eggs.getInstance().getGameTime() * 60);
	private static final GameBoard gameBoard = new GameBoard(ChatColor.GREEN + ChatColor.BOLD.toString() + "Eggs",
			DisplaySlot.SIDEBAR);

	static {
		gameBoard.getTabListFactory().injectProcessor(new TabTeamProcessor());
		gameBoard.addBlankLine();
		gameBoard.addLine(new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Time Left"));
		gameBoard.addLine(new TimeLeftElement());
		gameBoard.addBlankLine();
		gameBoard.addLine(new ConstantElement(ChatColor.GOLD + ChatColor.BOLD.toString() + "Your Coins"));
		gameBoard.addLine(new CoinsElement());
		gameBoard.addBlankLine();
		gameBoard.addLine(new TeamElement(EggsTeam.ORANGE));
		gameBoard.addLine(new TeamScoreElement(EggsTeam.ORANGE));
		gameBoard.addBlankLine();
		gameBoard.addLine(new TeamElement(EggsTeam.YELLOW));
		gameBoard.addLine(new TeamScoreElement(EggsTeam.YELLOW));
		gameBoard.addBlankLine();

		gameBoard.addLine(Eggs.IP_ELEMENT);

		gameTimer.setPrefix(Eggs.PREFIX);
		gameTimer.setOnTimerTick(tick -> onTimerTick());
		gameTimer.setOnTimerEnd(end -> {
			int orangeScore = EggsTeam.ORANGE.getScore();
			int yellowScore = EggsTeam.YELLOW.getScore();
			if (orangeScore > yellowScore) {
				GameEndStage.declareWinner(EggsTeam.ORANGE);
			} else if (yellowScore > orangeScore) {
				GameEndStage.declareWinner(EggsTeam.YELLOW);
			} else {
				GameEndStage.declareDraw();
			}
		});
	}

	public static void startGame() {
		Eggs plugin = Eggs.getInstance();
		plugin.setGameStage(GameStage.STARTING);
		MinigamesCore.setAcceptingPlayers(false);

		Bukkit.getOnlinePlayers().forEach(player -> {
			EggsTeam assignedTeam;
			if (EggsTeam.isInTeam(player)) {
				assignedTeam = EggsTeam.getTeam(player);
			} else {
				assignedTeam = EggsTeam.assignTeam(player);
				Team team = assignedTeam.getTeam();
				player.sendMessage(Eggs.formatMessage(
						ChatColor.GRAY + "You have been automatically assigned to the " + team.getColour().getChatColor()
								+ team.getName() + " Team" + ChatColor.GRAY + "!"));
			}

			teleportToSpawn(player, assignedTeam);
			gameBoard.render(Eggs.getInstance(), player, 20);
		});
		gameBoard.getTabListFactory().injectAllPlayers();

		// Takes away the team chooser items
		HotbarHandler.clearHotbarItems();
		HotbarHandler.giveHotbarItemsToAll();

		Timer timer = new Timer(Eggs.getInstance(), 10);
		timer.setOnTimerTick(tick -> {
			int secondsLeft = timer.getSecondsLeft();
			if (secondsLeft != 0 && (secondsLeft <= 5 || secondsLeft == 10)) {
				Bukkit.broadcastMessage(Eggs.formatMessage(
						ChatColor.GRAY + "The game will start in " + ChatColor.YELLOW + secondsLeft + " seconds..."));
				Eggs.pingAll();
			}
		});
		timer.setOnTimerEnd(end -> enterGame(plugin));
		timer.start();
	}

	private static void enterGame(Eggs plugin) {
		plugin.getGameWorldConfig().deleteSpawnWalls();
		gameTimer.start();

		HotbarHandler.setHotbarItem(0, new EggCannonItem());
		HotbarHandler.setHotbarItem(1, new ShopItem());
		HotbarHandler.giveHotbarItemsToAll();

		plugin.setGameStage(GameStage.IN_GAME);
	}

	public static void renderScoreboard(Player player) {
		gameBoard.render(Eggs.getInstance(), player, 20);
	}

	private static int yellowSpawnIndex = 0;
	private static int orangeSpawnIndex = 0;

	public static void teleportToSpawn(Player player, EggsTeam team) {
		player.teleport(getSpawnLocation(team));
	}

	public static Location getSpawnLocation(EggsTeam team) {
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

		return location;
	}

	private static void onTimerTick() {
		String message = null;
		int seconds = gameTimer.getSecondsLeft();
		if (seconds == 120) {
			message = ChatColor.GRAY + "The game will end in " + ChatColor.YELLOW + "2 minutes " + ChatColor.GRAY
					+ "and highest score wins!";
		} else if (seconds == 60) {
			message = ChatColor.GRAY + "There is only " + ChatColor.YELLOW + "1 minute left" + ChatColor.GRAY + "!";
		} else if (seconds != 0 && (seconds == 30 || seconds == 15 || seconds <= 5)) {
			Team team = null;
			if (EggsTeam.ORANGE.getScore() > EggsTeam.YELLOW.getScore()) {
				team = EggsTeam.ORANGE.getTeam();
			} else if (EggsTeam.YELLOW.getScore() > EggsTeam.ORANGE.getScore()) {
				team = EggsTeam.YELLOW.getTeam();
			}

			if (team == null) {
				message = ChatColor.GRAY + "The game will be a draw in " + ChatColor.YELLOW + seconds + " seconds...";
			} else {
				message = team.getColour().getChatColor() + team.getName() + " Team " + ChatColor.GRAY + "will win for "
						+ ChatColor.LIGHT_PURPLE + "highest score " + ChatColor.GRAY + "in " + ChatColor.GREEN + seconds
						+ " seconds...";
			}
		}

		if (message != null) {
			Bukkit.broadcastMessage(Eggs.formatMessage(message));
			Eggs.pingAll();
		}
	}

	public static void updateTabListFactory() {
		gameBoard.getTabListFactory().injectAllPlayers();
	}

}
