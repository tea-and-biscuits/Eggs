package uk.co.harieo.eggs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.games.GameStage;
import uk.co.harieo.minigames.menus.MenuItem;

public class HotbarHandler implements Listener {

	private static final Map<Integer, MenuItem> hotbarItems = new HashMap<>(9);

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (Eggs.getInstance().getGameStage() != GameStage.IN_GAME) {
			giveHotbarItems(event.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryInteract(InventoryClickEvent event) {
		Inventory inventory = event.getClickedInventory();
		if (inventory != null && inventory.getType() == InventoryType.PLAYER) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		Action action = event.getAction();
		if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
			int clickedSlot = inventory.getHeldItemSlot();
			ItemStack clickedItem = inventory.getItem(clickedSlot);
			if (hotbarItems.containsKey(clickedSlot) && clickedItem != null &&
					clickedItem.getType() == hotbarItems.get(clickedSlot).getItem().getType()) {
				hotbarItems.get(clickedSlot).onClick(player);
				event.setCancelled(true);
			}
		}
	}

	public static void giveHotbarItemsToAll() {
		Bukkit.getOnlinePlayers().forEach(HotbarHandler::giveHotbarItems);
	}

	public static void giveHotbarItems(Player player) {
		PlayerInventory inventory = player.getInventory();
		inventory.clear();

		EggsTeam team = EggsTeam.getTeam(player);
		if (team != null) {
			team.setChestplate(player);
		}

		for (int slot : hotbarItems.keySet()) {
			inventory.setItem(slot, hotbarItems.get(slot).getItem());
		}
	}

	public static void setHotbarItem(int slot, MenuItem item) {
		if (hotbarItems.containsKey(slot)) {
			hotbarItems.replace(slot, item);
		} else {
			hotbarItems.put(slot, item);
		}
	}

	public static void floodHotbar(MenuItem item) {
		clearHotbarItems();
		for (int i = 0; i < 9; i++) {
			setHotbarItem(i, item);
		}
	}

	public static void clearHotbarItems() {
		hotbarItems.clear();
	}

}
