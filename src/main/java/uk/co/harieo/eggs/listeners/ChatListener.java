package uk.co.harieo.eggs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.teams.Team;

public class ChatListener implements Listener {

	@EventHandler
	public void onAsyncChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		event.setCancelled(true); // We are totally overriding this event

		EggsTeam eggsTeam = EggsTeam.getTeam(player);

		String prefix = "";
		if (eggsTeam != null) {
			Team team = eggsTeam.getTeam();
			prefix = team.getChatColor() + "[" + team.getTeamName() + "] ";
		}

		Bukkit.broadcastMessage(
				prefix + ChatColor.RESET + player.getDisplayName() + " " + ChatColor.DARK_GRAY + Eggs.ARROWS
						+ ChatColor.WHITE + " ");
	}

}
