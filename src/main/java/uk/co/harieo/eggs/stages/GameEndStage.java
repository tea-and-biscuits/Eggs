package uk.co.harieo.eggs.stages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.purchasables.CoinsHandler;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.MinigamesCore;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.minigames.timing.Timer;

public class GameEndStage {

	public static final int WINNING_SCORE = 50;
	private static final GameBoard endingScoreboard = new GameBoard(
			ChatColor.GREEN + ChatColor.BOLD.toString() + "Game Over", DisplaySlot.SIDEBAR);

	public static boolean hasTeamWon(EggsTeam team) {
		return team.getScore() >= WINNING_SCORE;
	}

	public static void declareWinner(EggsTeam team) {
		Eggs plugin = Eggs.getInstance();
		if (plugin.getGameStage() == GameStage.IN_GAME) {
			plugin.setGameStage(GameStage.ENDING);

			EggsTeam opposingTeam;
			if (team == EggsTeam.ORANGE) {
				opposingTeam = EggsTeam.YELLOW;
			} else {
				opposingTeam = EggsTeam.ORANGE;
			}

			Team apiTeam = team.getTeam();
			Team opposingApiTeam = opposingTeam.getTeam();
			sendGlobalTitleAndMessage(
					apiTeam.getChatColor() + ChatColor.BOLD.toString() + apiTeam.getTeamName().toUpperCase()
							+ " TEAM WINS",
					ChatColor.GRAY + "They got " + apiTeam.getChatColor() + (team.getScore() - opposingTeam.getScore())
							+ " more points " + ChatColor.GRAY + "than the " + opposingApiTeam.getChatColor()
							+ opposingApiTeam.getTeamName() + " Team " + ChatColor.GRAY + "at " + opposingApiTeam
							.getChatColor() + opposingTeam.getScore());
			apiTeam.getOnlineTeamMembers().forEach(player ->
					sendTripleFirework(player, firework -> {
						FireworkMeta fireworkMeta = firework.getFireworkMeta();
						fireworkMeta.addEffect(FireworkEffect.builder().trail(true).withColor(apiTeam.getArmorColor())
								.with(Type.BALL).build());
						firework.setFireworkMeta(fireworkMeta);
					}));
			setGlobalScoreboard(setWinningScoreboard(apiTeam));
			selfDestruct();
		}
	}

	public static void declareDraw() {
		Eggs plugin = Eggs.getInstance();
		if (plugin.getGameStage() == GameStage.IN_GAME) {
			plugin.setGameStage(GameStage.ENDING);
			sendGlobalTitleAndMessage(ChatColor.GRAY + ChatColor.BOLD.toString() + "It's a Draw", null);
			setGlobalScoreboard(setDrawScoreboard());
			selfDestruct();
		}
	}

	private static void sendGlobalTitleAndMessage(String text, String subHeading) {
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.sendTitle(text, null, 20,
					7 * 20, 20);
			player.sendMessage("");
			player.sendMessage(Eggs.formatMessage(text));
			if (subHeading != null) {
				player.sendMessage(Eggs.formatMessage(subHeading));
			}
			player.sendMessage("");
		});
	}

	private static void sendTripleFirework(Player player, Consumer<Firework> metaConsumer) {
		for (int i = 0; i < 3; i++) {
			Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
			metaConsumer.accept(firework);
		}
	}

	private static GameBoard setWinningScoreboard(Team winningTeam) {
		endingScoreboard.addBlankLine();
		endingScoreboard.addLine(new ConstantElement(
				winningTeam.getChatColor() + ChatColor.BOLD.toString() + winningTeam.getTeamName() + " Team Wins"));
		endingScoreboard.addLine(new ConstantElement(ChatColor.GRAY + "To the Victors, the Spoils..."));
		endingScoreboard.addBlankLine();
		for (Player player : winningTeam.getOnlineTeamMembers()) {
			endingScoreboard.addLine(new ConstantElement(winningTeam.getChatColor() + player.getName()));
		}
		return endingScoreboard;
	}

	private static GameBoard setDrawScoreboard() {
		endingScoreboard.addBlankLine();
		endingScoreboard.addLine(new ConstantElement(ChatColor.GRAY + ChatColor.BOLD.toString() + "It's a Draw"));
		endingScoreboard.addLine(new ConstantElement(ChatColor.WHITE + "Better luck next time!"));
		endingScoreboard.addBlankLine();
		endingScoreboard.addLine(
				new ConstantElement(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Most Victorious Players"));

		// Gets 3 players with the highest coin count to display as MVPs (at least someone will get a victory)
		List<Player> MVPs = new ArrayList<>(3);
		for (int i = 0; i < 3; i++) {
			Player mvp = null;
			int mvpCoins = 0;
			for (Player player : Bukkit.getOnlinePlayers()) {
				int coins = CoinsHandler.getCoins(player);
				if ((coins > mvpCoins || mvp == null) && !MVPs.contains(player)) {
					mvp = player;
					mvpCoins = coins;
				}
			}

			MVPs.add(mvp);
		}

		for (int i = 1; i <= 3; i++) {
			Player indexPlayer = MVPs.get(i - 1);
			String playerName = ChatColor.WHITE + (indexPlayer != null ? indexPlayer.getName() : "Nobody");
			endingScoreboard.addLine(
					new ConstantElement(ChatColor.GOLD + ChatColor.BOLD.toString() + "#" + i + " " + playerName));
		}

		return endingScoreboard;
	}

	private static void setGlobalScoreboard(GameBoard gameBoard) {
		Bukkit.getOnlinePlayers().forEach(player -> gameBoard.render(Eggs.getInstance(), player, 20 * 20));
	}

	private static void selfDestruct() {
		Timer timer = new Timer(Eggs.getInstance(), 15);
		timer.setOnTimerTick(tick -> {
			int seconds = timer.getSecondsLeft();
			if (seconds == 5) {
				MinigamesCore.sendAllPlayersToFallbackServer();
			}
		});
		timer.setOnTimerEnd(end -> Bukkit.getServer().shutdown());
		timer.start();
	}

}
