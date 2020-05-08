package uk.co.harieo.eggs.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.teams.Team;

public class TeamCommand implements CommandExecutor {

	private static final Map<UUID, LocalTime> lastAccess = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length < 1) {
				player.sendMessage(Eggs.formatMessage(
						ChatColor.RED + "Please select either orange or yellow: /team <orange/yellow>"));
			} else if (Eggs.getInstance().getGameStage() != GameStage.LOBBY) {
				player.sendMessage(Eggs.formatMessage(ChatColor.RED + "You can only do this in the lobby!"));
			} else if (lastAccess.containsKey(player.getUniqueId())
					&& LocalTime.now().toSecondOfDay() - lastAccess.get(player.getUniqueId()).toSecondOfDay() <= 5) {
				player.sendMessage(
						Eggs.formatMessage(ChatColor.RED + "Please wait 5 seconds before switching teams again!"));
			} else {
				String teamName = args[0];

				EggsTeam eggsTeam;
				if (teamName.equalsIgnoreCase("orange")) {
					eggsTeam = EggsTeam.ORANGE;
				} else if (teamName.equalsIgnoreCase("yellow")) {
					eggsTeam = EggsTeam.YELLOW;
				} else {
					player.sendMessage(Eggs.formatMessage(
							ChatColor.RED + "Unknown team, please select either orange or yellow: " + teamName));
					return false;
				}

				if (EggsTeam.isInTeam(player) && EggsTeam.getTeam(player) == eggsTeam) {
					player.sendMessage(Eggs.formatMessage(ChatColor.RED + "You are already on that team!"));
				} else {
					Team apiTeam = eggsTeam.getTeam();
					EggsTeam oppositeTeam = eggsTeam == EggsTeam.ORANGE ? EggsTeam.YELLOW : EggsTeam.ORANGE;
					if (apiTeam.countMembers() > oppositeTeam.getTeam().countMembers() + 1) {
						player.sendMessage(
								Eggs.formatMessage(ChatColor.RED + "There are too many people on that team!"));
					} else {
						EggsTeam.setTeam(player, eggsTeam);
						player.sendMessage(Eggs.formatMessage(
								ChatColor.GRAY + "You have joined the " + apiTeam.getChatColor() + apiTeam.getTeamName()
										+ " Team!"));
					}

					lastAccess.put(player.getUniqueId(), LocalTime.now());
				}
			}
		} else {
			sender.sendMessage("You must be a player to do that!");
		}
		return false;
	}

}
