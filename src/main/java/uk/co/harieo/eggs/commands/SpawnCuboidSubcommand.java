package uk.co.harieo.eggs.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.config.GameWorldConfig;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.minigames.teams.Team;

public class SpawnCuboidSubcommand implements Listener {

	private static final SpawnCuboidSubcommand instance = new SpawnCuboidSubcommand();
	private static final Map<UUID, PreCuboid> cuboidsInProgress = new HashMap<>();

	private SpawnCuboidSubcommand() {
		Bukkit.getPluginManager().registerEvents(this, Eggs.getInstance());
	}

	void onCommand(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(
					Eggs.formatMessage(ChatColor.RED + "Insufficient Arguments. Expected: /maps wall <team/position>"));
		} else {
			String subCommand = args[0];
			switch (subCommand.toLowerCase()) {
				case "orange":
					setTeam(player, EggsTeam.ORANGE);
					break;
				case "yellow":
					setTeam(player, EggsTeam.YELLOW);
					break;
				case "one":
				case "1":
				case "pos1":
					setPosition(player, Position.ONE);
					break;
				case "two":
				case "2":
				case "pos2":
					setPosition(player, Position.TWO);
					break;
				case "cancel":
					cuboidsInProgress.remove(player.getUniqueId());
					player.sendMessage(Eggs.formatMessage(
							ChatColor.GRAY + "You are no longer editing any spawn wall and can click freely!"));
					break;
				case "save":
					save(player);
					break;
				default:
					player.sendMessage(Eggs.formatMessage(ChatColor.RED + "Unknown Sub-command: " + subCommand));
			}
		}
	}

	private void setTeam(Player player, EggsTeam team) {
		UUID uuid = player.getUniqueId();
		if (cuboidsInProgress.containsKey(uuid)) {
			player.sendMessage(Eggs.formatMessage(
					ChatColor.RED + "You are already editing a cuboid for the " +
							cuboidsInProgress.get(uuid).getTeam().getTeam().getTeamName()
							+ ". Use '/maps wall cancel' to abort editing."));
		} else {
			cuboidsInProgress.put(uuid, new PreCuboid(team, player.getWorld()));
			Team apiTeam = team.getTeam();
			player.sendMessage(Eggs.formatMessage(
					ChatColor.GRAY + "You are now editing for the " + apiTeam.getChatColor() + apiTeam.getTeamName()
							+ " Team. " + ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/maps wall <pos1/pos2> "
							+ ChatColor.GRAY + "to set the 2 corners!"));
		}
	}

	private void setPosition(Player player, Position position) {
		UUID uuid = player.getUniqueId();
		if (cuboidsInProgress.containsKey(uuid)) {
			cuboidsInProgress.get(uuid).setEditingPosition(position);
			player.sendMessage(Eggs.formatMessage(
					ChatColor.GRAY + "You are now editing " + ChatColor.GREEN + "Position " + position.name()
							+ ChatColor.GRAY + ", use " + ChatColor.YELLOW + "Right Click " + ChatColor.GRAY
							+ "on a block to set the corner!"));
		} else {
			player.sendMessage(Eggs.formatMessage(ChatColor.RED
					+ "You are not editing the wall for any team. Use '/maps wall <orange/yellow>' to start editing their spawn wall!"));
		}
	}

	private void save(Player player) {
		UUID uuid = player.getUniqueId();
		if (cuboidsInProgress.containsKey(uuid)) {
			PreCuboid preCuboid = cuboidsInProgress.get(uuid);
			Location corner1 = preCuboid.getCorner1();
			Location corner2 = preCuboid.getCorner2();

			String missingPosition = null;
			if (corner1 == null) {
				missingPosition = "pos1";
			} else if (corner2 == null) {
				missingPosition = "pos2";
			}

			if (missingPosition != null) {
				player.sendMessage(Eggs.formatMessage(
						ChatColor.RED + "Missing position: " + missingPosition + ". Use '/maps wall " + missingPosition
								+ "' to set!"));
				return;
			}

			EggsTeam team = preCuboid.getTeam();
			MapImpl map = MapImpl.get(player.getWorld());

			boolean isOverwriting = false;
			for (LocationPair pair : map.getLocationsByKey(GameWorldConfig.WALL_CORNER_KEY)) {
				if (pair.getValue().equals(team.name())) {
					isOverwriting = true;
					map.removeLocation(pair);
				}
			}

			if (isOverwriting) {
				player.sendMessage(ChatColor.YELLOW + "Please be advised: " + ChatColor.GRAY
						+ "You are overwriting a previously saved wall for this team.");
			}

			map.addLocation(corner1, GameWorldConfig.WALL_CORNER_KEY, team.name());
			map.addLocation(corner2, GameWorldConfig.WALL_CORNER_KEY, team.name());
			cuboidsInProgress.remove(uuid);
			player.sendMessage(Eggs.formatMessage(
					ChatColor.GREEN + "You have saved this spawn wall location. " + ChatColor.GRAY
							+ "Note: You still need to do " + ChatColor.YELLOW + "/maps commit " + ChatColor.GRAY
							+ "to make it permanent!"));
		} else {
			player.sendMessage(Eggs.formatMessage(ChatColor.RED + "There is nothing to save!"));
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		if (cuboidsInProgress.containsKey(uuid) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			PreCuboid preCuboid = cuboidsInProgress.get(uuid);
			if (!player.getWorld().equals(preCuboid.getWorld())) {
				player.sendMessage(Eggs.formatMessage(
						ChatColor.RED + "You cannot set a position in a different world to the original!"));
			} else if (preCuboid.getEditingPosition() != null) {
				Block block = event.getClickedBlock();
				if (block != null) {
					String position;
					if (preCuboid.getEditingPosition() == Position.ONE) {
						preCuboid.setCorner1(block.getLocation());
						position = "1";
					} else if (preCuboid.getEditingPosition() == Position.TWO) {
						preCuboid.setCorner2(block.getLocation());
						position = "2";
					} else {
						throw new IllegalStateException("Position is invalid");
					}

					if (preCuboid.getCorner1() != null && preCuboid.getCorner2() != null) {
						player.sendMessage(Eggs.formatMessage(
								ChatColor.GRAY + "You have set both positions, you may wish to do " + ChatColor.YELLOW
										+ "/maps wall save " + ChatColor.GRAY + "to save them!"));
					} else {
						player.sendMessage(Eggs.formatMessage(
								ChatColor.GRAY + "Set " + ChatColor.GREEN + "Position " + position + ChatColor.GRAY
										+ " to that block!"));
					}
				}
			}
		}
	}

	public static SpawnCuboidSubcommand getInstance() {
		return instance;
	}

	enum Position {
		ONE, TWO
	}

}
