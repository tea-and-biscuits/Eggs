package uk.co.harieo.eggs.purchasables.handlers;

import org.bukkit.*;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.listeners.CombatListener;

public class QuackAttackRunnable implements Runnable {

	private static final String[] NAMES = {"Steve", "Bob", "Jeff", "Max", "Janett", "Lily", "Gemma", "Margret"};

	private final Player player;

	private BukkitTask task;
	private int chickensLeft = 10;
	private final List<Chicken> spawnedChickens = new ArrayList<>();

	public QuackAttackRunnable(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		if (chickensLeft <= 0) {
			task.cancel();
			Bukkit.getScheduler().runTaskLater(Eggs.getInstance(), this::selfDestruct, 20);
		} else {
			Location location = player.getLocation().clone();

			int randomX = Eggs.RANDOM.nextInt(9) + 1;
			int randomZ = Eggs.RANDOM.nextInt(9) + 1;
			boolean negativeX = Eggs.RANDOM.nextInt(100) % 2 == 0;
			boolean negativeZ = Eggs.RANDOM.nextInt(100) % 2 == 0;
			location.add(negativeX ? -randomX : randomX, 3, negativeZ ? -randomZ : randomZ);

			Chicken chicken = (Chicken) player.getWorld().spawnEntity(location, EntityType.CHICKEN);
			chicken.setCustomName(
					ChatColor.YELLOW + ChatColor.BOLD.toString() + NAMES[Eggs.RANDOM.nextInt(NAMES.length)]);
			chicken.setBreedCause(player.getUniqueId()); // This is to transmit the summoner's UUID to the listener
			spawnedChickens.add(chicken);
			chickensLeft--;
		}
	}

	public void start() {
		this.task = Bukkit.getScheduler().runTaskTimer(Eggs.getInstance(), this, 0, 5);
		CombatListener.disableCoins(player);
	}

	private void selfDestruct() {
		for (Chicken chicken : spawnedChickens) {
			World world = chicken.getWorld();
			Location location = chicken.getLocation();
			world.createExplosion(location, 2F, false, false, chicken);
			world.playSound(location, Sound.ENTITY_CHICKEN_DEATH, 1F, 1F);
			chicken.remove();
		}

		spawnedChickens.clear(); // Prevents any accidental repeats
		CombatListener.enableCoins(player);
	}

}
