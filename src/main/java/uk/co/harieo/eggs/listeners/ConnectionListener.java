package uk.co.harieo.eggs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.stages.GameStartStage;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.timing.LobbyTimer;

public class ConnectionListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Eggs plugin = Eggs.getInstance();

		player.setGameMode(GameMode.SURVIVAL);
		player.setFoodLevel(20);
		player.setHealth(20);

		boolean isLobbyStage = plugin.getGameStage() == GameStage.LOBBY;
		if (isLobbyStage || plugin.getGameStage() == GameStage.ERROR) {
			event.setJoinMessage(Eggs.formatMessage(
					ChatColor.GREEN + player.getName() + ChatColor.GRAY + " comes in with a " + ChatColor.YELLOW
							+ "splat" + ChatColor.GRAY + "!"));
			player.teleport(plugin.getLobbyWorld().getSpawnLocation());
			plugin.renderLobbyScoreboard(player);

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
			player.setGameMode(GameMode.SPECTATOR);
			GameStartStage.renderScoreboard(player);

			boolean isSpawned = false;
			for (Location location : plugin.getGameWorldConfig().getOrangeSpawns()) {
				player.teleport(location);
				isSpawned = true; // This won't happen if there are no spawns available
			}

			if (!isSpawned) {
				player.teleport(plugin.getLobbyWorld().getSpawnLocation());
			}

			player.sendMessage(
					Eggs.formatMessage(ChatColor.GRAY + "You have joined mid-game so we've made you a spectator!"));
			Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.hidePlayer(plugin, player));
		}
	}

}
