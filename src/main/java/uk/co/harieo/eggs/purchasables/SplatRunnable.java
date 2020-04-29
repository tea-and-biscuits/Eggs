package uk.co.harieo.eggs.purchasables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import uk.co.harieo.eggs.Eggs;

public class SplatRunnable implements Runnable {

	private final Player player;
	private int ticks = 20 * 10; // Starts at 10 seconds

	private BukkitTask task;

	public SplatRunnable(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		if (ticks <= 0) {
			task.cancel();
		} else {
			player.launchProjectile(Egg.class);
			ticks--;
		}
	}

	public void start() {
		this.task = Bukkit.getScheduler().runTaskTimer(Eggs.getInstance(), this, 0, 1);
	}

}
