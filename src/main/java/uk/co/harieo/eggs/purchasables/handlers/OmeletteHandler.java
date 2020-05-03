package uk.co.harieo.eggs.purchasables.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.listeners.CombatListener;

public class OmeletteHandler {

	private static final Map<UUID, BukkitTask> omeletteUsers = new HashMap<>();

	public static void onRegularEggLaunch(Player player) {
		if (omeletteUsers.containsKey(player.getUniqueId())) {
			Vector playerVector = player.getLocation().getDirection();
			Vector side = playerVector.clone().crossProduct(new Vector(playerVector.getX(), 0, playerVector.getZ()));
			side = side.normalize();
			Vector axis = playerVector.clone().crossProduct(side);
			player.launchProjectile(Egg.class, playerVector.clone().rotateAroundAxis(axis, Math.toRadians(20)));
			player.launchProjectile(Egg.class, playerVector.clone().rotateAroundAxis(axis, Math.toRadians(-20)));
		}
	}

	public static void activateOmelette(Player player) {
		UUID uuid = player.getUniqueId();
		if (omeletteUsers.containsKey(uuid)) {
			player.sendMessage(Eggs.formatMessage(
					ChatColor.GRAY + "You already had " + ChatColor.GREEN + "Omelette " + ChatColor.GRAY
							+ "so we've reset the timer to " + ChatColor.YELLOW + "10 seconds!"));
			omeletteUsers.get(uuid).cancel(); // Make sure it doesn't cancel half way through
			omeletteUsers.remove(uuid);
		}

		CombatListener.setDamage(player, 10);
		// Stops the ability after 10 seconds
		BukkitTask task = Bukkit.getScheduler()
				.runTaskLater(Eggs.getInstance(), () -> {
					CombatListener.resetDamage(player);
					omeletteUsers.remove(player.getUniqueId());
					player.sendMessage(Eggs.formatMessage(ChatColor.RED + "Omelette has worn off!"));
				}, 20 * 10);
		omeletteUsers.put(uuid, task);
	}

}
