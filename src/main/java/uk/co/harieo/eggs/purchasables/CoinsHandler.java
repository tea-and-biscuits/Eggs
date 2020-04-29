package uk.co.harieo.eggs.purchasables;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoinsHandler {

	private static final Map<UUID, Integer> coins = new HashMap<>();

	public static int getCoins(Player player) {
		return coins.getOrDefault(player.getUniqueId(), 0);
	}

	public static void setCoins(Player player, int value) {
		UUID uuid = player.getUniqueId();
		if (coins.containsKey(uuid)) {
			coins.replace(uuid, value);
		} else {
			coins.put(uuid, value);
		}
	}

	public static void addCoins(Player player, int amount) {
		setCoins(player, getCoins(player) + amount);
	}

	public static void subtractCoins(Player player, int amount) {
		addCoins(player, -amount); // 5 + -2 = 3, subtraction!
	}

	public static boolean hasCoins(Player player, int amount) {
		return getCoins(player) >= amount;
	}

}
