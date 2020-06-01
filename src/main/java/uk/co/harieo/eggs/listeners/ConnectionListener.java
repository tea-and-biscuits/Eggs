package uk.co.harieo.eggs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.stages.GameEndStage;
import uk.co.harieo.eggs.stages.GameStartStage;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.timing.LobbyTimer;

public class ConnectionListener implements Listener {

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (Eggs.getInstance().getGameStage() != GameStage.LOBBY && !event.getPlayer()
				.hasPermission("quacktopia.minigames.spectate")) {
			event.disallow(Result.KICK_OTHER, "This game is in-progress");
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Eggs plugin = Eggs.getInstance();

		player.setFoodLevel(20);

		AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if (maxHealthAttribute != null) {
			maxHealthAttribute.setBaseValue(4);
		}
		player.setHealth(4);

		boolean isLobbyStage = plugin.getGameStage() == GameStage.LOBBY;
		if (isLobbyStage || plugin.getGameStage() == GameStage.ERROR) {
			event.setJoinMessage(Eggs.formatMessage(
					ChatColor.GREEN + player.getName() + ChatColor.GRAY + " comes in with a " + ChatColor.YELLOW
							+ "splat" + ChatColor.GRAY + "!"));
			player.teleport(plugin.getLobbyWorld().getSpawnLocation());
			plugin.renderLobbyScoreboard(player);
			player.setGameMode(GameMode.SURVIVAL);

			if (isLobbyStage) { // Don't mess with the timer on error
				LobbyTimer timer = plugin.getLobbyTimer();
				int playerCount = Bukkit.getOnlinePlayers().size();
				// Update the lobby timer based on how many players are online
				if (playerCount >= plugin.getMaxPlayers()) {
					timer.updateToFull();
				} else if (playerCount >= plugin.getOptimalPlayers()) {
					timer.updateToOptimal();
				} else {
					timer.updateToInsufficient();
				}
			}
		} else {
			event.setJoinMessage(null);
			GameStartStage.renderScoreboard(player);

			boolean isSpawned = false;
			for (Location location : plugin.getGameWorldConfig().getOrangeSpawns()) {
				player.teleport(location);
				isSpawned = true; // This won't happen if there are no spawns available
			}

			if (!isSpawned) {
				player.teleport(plugin.getLobbyWorld().getSpawnLocation());
			}

			Bukkit.getScheduler().runTaskLater(Eggs.getInstance(), () -> player.setGameMode(GameMode.SPECTATOR), 20 * 2);

			player.sendMessage(
					Eggs.formatMessage(ChatColor.GRAY + "You have joined mid-game so we've made you a spectator!"));
			player.getInventory().clear();
			Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.hidePlayer(plugin, player));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (Eggs.getInstance().getGameStage() == GameStage.IN_GAME) {
			int newPlayerCount = Bukkit.getOnlinePlayers().size() - 1;
			if (newPlayerCount == 1) {
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) { // Need to find the last remaining player
					if (!onlinePlayer.equals(player)) { // Make sure it's not the player which is quitting
						EggsTeam team = EggsTeam.getTeam(onlinePlayer);
						if (team != null) {
							GameEndStage.declareWinner(team);
						}
					}
				}
			} else if (newPlayerCount < 1) {
				GameEndStage.declareDraw();
			}
		}
	}

}
