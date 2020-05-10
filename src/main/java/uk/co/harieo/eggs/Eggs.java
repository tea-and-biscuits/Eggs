package uk.co.harieo.eggs;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.List;
import java.util.Random;
import uk.co.harieo.eggs.commands.*;
import uk.co.harieo.eggs.config.GameConfig;
import uk.co.harieo.eggs.config.GameWorldConfig;
import uk.co.harieo.eggs.listeners.*;
import uk.co.harieo.eggs.purchasables.handlers.OmeletteHandler;
import uk.co.harieo.eggs.scoreboard.PlayerCountElement;
import uk.co.harieo.eggs.stages.GameStartStage;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.games.Minigame;
import uk.co.harieo.minigames.menus.MenuItem;
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

		gameConfig = new GameConfig(this);
		if (getGameWorldConfig().isLoaded()) {
			gameStage = GameStage.LOBBY;
		} else {
			gameStage = GameStage.ERROR;
		}

		lobbyTimer = new LobbyTimer(this);
		List<String> timerMessages = gameConfig.getTimerMessages();
		if (!timerMessages.isEmpty()) { // If they are empty, it will use the default message instead
			lobbyTimer.setCountdownMessages(timerMessages);
		}
		lobbyTimer.setOnTimerEnd(end -> GameStartStage.startGame());
		lobbyTimer.setPrefix(PREFIX);

		lobbyScoreboard = createLobbyScoreboard();
		HotbarHandler.setHotbarItem(3, new MenuItem(Material.ORANGE_WOOL)
				.setName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Join Orange Team")
				.setOnClick(player -> player.chat("/team orange")));
		HotbarHandler.setHotbarItem(5, new MenuItem(Material.YELLOW_WOOL)
				.setName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Join Yellow Team")
				.setOnClick(player -> player.chat("/team yellow")));

		registerListeners(new ConnectionListener(), new ChatListener(), new WorldProtectionListener(),
				new HotbarHandler(), new CombatListener());
		registerCommand(new ForceStartCommand(), "force");
		registerCommand(new MapCommand(), "maps", "map");
		registerCommand(new TeamCommand(), "team");
		registerCommand(new ShopCommand(), "shop");
		registerCommand(new CoinsCommand(), "coins");
	}

	private GameBoard createLobbyScoreboard() {
		GameBoard gameBoard = new GameBoard(ChatColor.GREEN + ChatColor.BOLD.toString() + "Eggs", DisplaySlot.SIDEBAR);
		gameBoard.addBlankLine();
		gameBoard.addLine(new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Players"));
		gameBoard.addLine(new PlayerCountElement());
		gameBoard.addBlankLine();
		gameBoard.addLine(new ConstantElement(ChatColor.GOLD + ChatColor.BOLD.toString() + "Time Left"));
		gameBoard.addLine(lobbyTimer);
		if (gameStage == GameStage.ERROR) {
			gameBoard.addBlankLine();
			gameBoard.addLine(new ConstantElement(ChatColor.RED + ChatColor.BOLD.toString() + "Server Error"));
			gameBoard.addLine(new ConstantElement("Game Offline"));
		}
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

	public void setGameStage(GameStage gameStage) {
		this.gameStage = gameStage;
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

	public static void pingAll() {
		Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(),
				Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.5F));
	}

}
