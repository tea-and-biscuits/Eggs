package uk.co.harieo.eggs;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Random;
import uk.co.harieo.eggs.commands.ForceStartCommand;
import uk.co.harieo.eggs.config.GameConfig;
import uk.co.harieo.eggs.config.GameWorldConfig;
import uk.co.harieo.eggs.listeners.ChatListener;
import uk.co.harieo.eggs.listeners.ConnectionListener;
import uk.co.harieo.eggs.scoreboard.PlayerCountElement;
import uk.co.harieo.eggs.stages.GameStartStage;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.games.Minigame;
import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;
import uk.co.harieo.minigames.timing.LobbyTimer;

public class Eggs extends Minigame {

	public static final char ARROWS = '»';
	public static final String PREFIX =
			ChatColor.GREEN.toString() + ChatColor.BOLD + "Eggs " + ChatColor.DARK_GRAY + ARROWS + " ";
	public static final ConstantElement IP_ELEMENT = new ConstantElement(
			ChatColor.GRAY + ChatColor.BOLD.toString() + "play." + ChatColor.YELLOW + ChatColor.BOLD
					+ "Quacktopia" + ChatColor.GRAY + ChatColor.BOLD + ".com");
	public static final Random RANDOM = new Random();
	private static Eggs instance;

	private LobbyTimer lobbyTimer;
	private GameBoard lobbyScoreboard;
	private GameStage gameStage = GameStage.STARTING;
	private GameConfig gameConfig;

	@Override
	public void onEnable() {
		instance = this;

		lobbyTimer = new LobbyTimer(this);
		lobbyTimer.setOnTimerEnd(end -> GameStartStage.startGame());

		lobbyScoreboard = createLobbyScoreboard();
		gameStage = GameStage.LOBBY;

		gameConfig = new GameConfig(this);

		registerListeners(new ConnectionListener(), new ChatListener());
		registerCommand(new ForceStartCommand(), "force");
	}

	private GameBoard createLobbyScoreboard() {
		GameBoard gameBoard = new GameBoard(ChatColor.GREEN + ChatColor.BOLD.toString() + "Eggs", DisplaySlot.SIDEBAR);
		gameBoard.addBlankLine();
		gameBoard.addLine(new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Players"));
		gameBoard.addLine(new PlayerCountElement());
		gameBoard.addBlankLine();
		gameBoard.addLine(new ConstantElement(ChatColor.GOLD + ChatColor.BOLD.toString() + "Time Left"));
		gameBoard.addLine(lobbyTimer);
		gameBoard.addBlankLine();
		gameBoard.addLine(IP_ELEMENT);
		return gameBoard;
	}

	@Override
	public String getMinigameName() {
		return "Eggs";
	}

	@Override
	public int getMaxPlayers() {
		return 10;
	}

	@Override
	public int getOptimalPlayers() {
		return 6;
	}

	public void renderLobbyScoreboard(Player player) {
		lobbyScoreboard.render(this, player, 20);
	}

	public LobbyTimer getLobbyTimer() {
		return lobbyTimer;
	}

	public GameStage getGameStage() {
		return gameStage;
	}

	public World getLobbyWorld() {
		return gameConfig.getLobbyWorld();
	}

	public GameWorldConfig getGameWorldConfig() {
		return gameConfig.getGameWorldConfig();
	}

	public int getGameTime() {
		return gameConfig.getMinutesOfGame();
	}

	public static Eggs getInstance() {
		return instance;
	}

	public static String formatMessage(String message) {
		return PREFIX + message;
	}

}
