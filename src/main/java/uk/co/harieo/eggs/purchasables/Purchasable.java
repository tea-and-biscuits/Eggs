package uk.co.harieo.eggs.purchasables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.listeners.CombatListener;

public enum Purchasable {

	WADDLE("Waddle", "A 10 second speed boost", 20,
			player -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 2))),
	OMELETTE("Omelette", "A spray of higher-damage eggs for 10 seconds", 50,
			player -> {
				CombatListener.setDamage(player, 10);
				Bukkit.getScheduler()
						.runTaskLater(Eggs.getInstance(), () -> CombatListener.resetDamage(player), 20 * 10);
			}),
	SPLAT("Splat", "A rapid-fire of eggs for 10 seconds", 60, player -> new SplatRunnable(player).start()),
	QUACK_ATTACK("Quack Attack", "Rain chickens from the sky which explode enemies", 100,
			player -> new QuackAttackRunnable(player).start());

	private final String name;
	private final String description;
	private final int cost;
	private final Consumer<Player> onPurchaseConsumer;

	Purchasable(String name, String description, int cost, Consumer<Player> onPurchase) {
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.onPurchaseConsumer = onPurchase;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getCost() {
		return cost;
	}

	public void activate(Player player) {
		onPurchaseConsumer.accept(player);
	}

}
