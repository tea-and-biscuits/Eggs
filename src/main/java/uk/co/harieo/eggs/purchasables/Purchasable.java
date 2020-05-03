package uk.co.harieo.eggs.purchasables;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;
import uk.co.harieo.eggs.purchasables.handlers.OmeletteHandler;
import uk.co.harieo.eggs.purchasables.handlers.QuackAttackRunnable;
import uk.co.harieo.eggs.purchasables.handlers.SplatRunnable;

public enum Purchasable {

	WADDLE("Waddle", "A 10 second speed boost", 20,
			player -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 2)), Material.FEATHER),
	OMELETTE("Omelette", "A spray of higher-damage eggs for 10 seconds", 50,
			OmeletteHandler::activateOmelette, Material.IRON_SHOVEL),
	SPLAT("Splat", "A rapid-fire of eggs for 10 seconds", 60, player -> new SplatRunnable(player).start(), Material.EGG),
	QUACK_ATTACK("Quack Attack", "Rain chickens from the sky which explode enemies", 100,
			player -> new QuackAttackRunnable(player).start(), Material.COOKED_CHICKEN);

	private final String name;
	private final String description;
	private final int cost;
	private final Consumer<Player> onPurchaseConsumer;
	private final Material material;

	Purchasable(String name, String description, int cost, Consumer<Player> onPurchase, Material displayMaterial) {
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.onPurchaseConsumer = onPurchase;
		this.material = displayMaterial;
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

	public Material getDisplayMaterial() {
		return material;
	}

	public void activate(Player player) {
		onPurchaseConsumer.accept(player);
	}

}
