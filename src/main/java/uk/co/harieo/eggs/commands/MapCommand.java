package uk.co.harieo.eggs.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.config.GameWorldConfig;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.minigames.teams.Team;

public class MapCommand implements CommandExecutor {

	private static final List<String> subCommandList = new ArrayList<>();
	private static final String subCommandString;

	private static final String SET_SPAWN = "setspawn";
	private static final String DELETE_SPAWN = "deletespawn";
	private static final String INFO = "info";
	private static final String AUTHOR = "author";
	private static final String SET_NAME = "setname";
	private static final String COMMIT = "commit";
	private static final String CUBOID = "wall";

	static {
		subCommandList.add(SET_SPAWN);
		subCommandList.add(DELETE_SPAWN);
		subCommandList.add(INFO);
		subCommandList.add(AUTHOR);
		subCommandList.add(SET_NAME);
		subCommandList.add(COMMIT);
		subCommandList.add(CUBOID);

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < subCommandList.size(); i++) {
			builder.append(subCommandList.get(i));
			if (i + 2 == subCommandList.size()) { // If i + 1 is the last applicable
				builder.append(" or ");
			} else if (i + 1 < subCommandList.size()) {
				builder.append(", ");
			}
		}
		subCommandString = builder.toString();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!sender.hasPermission("quacktopia.minigames.maps")) {
			sender.sendMessage(Eggs.formatMessage(ChatColor.RED + "You do not have permission to do that!"));
		} else if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can set locations!");
		} else {
			Player player = (Player) sender;
			if (args.length < 1) {
				player.sendMessage(
						Eggs.formatMessage(ChatColor.RED + "Insufficient Arguments. Expected: /maps <type>"));
			} else {
				String subCommand = args[0];
				boolean failed = false;
				if (subCommand.equalsIgnoreCase(SET_SPAWN)) {
					setSpawn(player, args);
				} else if (subCommand.equalsIgnoreCase(AUTHOR)) {
					author(player, args);
				} else if (subCommand.equalsIgnoreCase(INFO)) {
					info(player);
				} else if (subCommand.equalsIgnoreCase(SET_NAME)) {
					setName(player, args);
				} else if (subCommand.equalsIgnoreCase(COMMIT)) {
					commit(player);
				} else if (subCommand.equals(DELETE_SPAWN)) {
					deleteSpawn(player);
				} else if (subCommand.equals(CUBOID)) {
					SpawnCuboidSubcommand.getInstance().onCommand(player,
							args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);
				} else {
					failed = true;
				}

				if (failed) {
					sender.sendMessage(Eggs.formatMessage(
							ChatColor.RED + "Unrecognised sub-command: " + subCommand + ". Use " + subCommandString));
				}
			}
		}

		return false;
	}

	private void info(Player player) {
		MapImpl map = MapImpl.get(player.getWorld());

		player.sendMessage("");
		player.sendMessage(Eggs.formatMessage(ChatColor.YELLOW + "Eggs Map Information"));

		boolean validSpawns =
				checkSpawns(map.getLocationsByKey(GameWorldConfig.ORANGE_SPAWNS_KEY), player, "orange") &&
						checkSpawns(map.getLocationsByKey(GameWorldConfig.YELLOW_SPAWNS_KEY), player, "yellow");
		boolean validWalls =
				countCorners(player, map, EggsTeam.ORANGE) >= 2 && countCorners(player, map, EggsTeam.YELLOW) >= 2;
		boolean hasName = map.getFullName() != null;
		boolean hasAuthor = !map.getAuthors().isEmpty();

		if (!validSpawns) {
			player.sendMessage(
					Eggs.formatMessage(ChatColor.RED + "The map is not valid due to an issue with spawn locations!"));
		} else if (!validWalls) {
			player.sendMessage(Eggs.formatMessage(
					ChatColor.RED + "The map is not valid due to an issue with wall corner placements!"));
		} else if (!hasName) {
			player.sendMessage(Eggs.formatMessage(ChatColor.RED + "The map doesn't have a name! (/maps setname <name>)"));
		} else if (!hasAuthor) {
			player.sendMessage(Eggs.formatMessage(ChatColor.RED + "The map doesn't have any authors! (/maps author add <name>)"));
		} else {
			player.sendMessage(
					Eggs.formatMessage(ChatColor.GREEN + "The map is ready to be commited with /maps commit"));
		}

		player.sendMessage("");
	}

	private boolean checkSpawns(List<LocationPair> spawns, Player player, String teamName) {
		int spawnCount = spawns.size();
		if (spawnCount == 0) {
			player.sendMessage(
					Eggs.formatMessage(ChatColor.RED + "There are no places for the " + teamName + " team to spawn"));
			return false;
		} else if (spawnCount < 5) {
			player.sendMessage(
					Eggs.formatMessage(
							ChatColor.YELLOW + "There are less than 5 places for the " + teamName + " team to spawn"));
			return true;
		} else {
			player.sendMessage(Eggs.formatMessage(
					ChatColor.GREEN + "The " + teamName + " team has at least 5 spawns, one for each player."));
			return true;
		}
	}

	private int countCorners(Player player, MapImpl map, EggsTeam team) {
		int count = 0;
		for (LocationPair pair : map.getLocationsByKey(GameWorldConfig.WALL_CORNER_KEY)) {
			if (pair.getValue().equals(team.name())) {
				count++;
			}
		}

		String teamName = team.getTeam().getTeamName();
		if (count < 2) {
			player.sendMessage(Eggs.formatMessage(
					ChatColor.RED + "There are only " + count + " corner(s) for the " + teamName + " team's spawn wall!"));
		} else {
			player.sendMessage(
					Eggs.formatMessage(ChatColor.GREEN + "There are 2 corners for the " + teamName + " team!"));
		}

		return count;
	}

	private void setSpawn(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(Eggs.formatMessage(
					ChatColor.RED
							+ "Please specify a team to set the island spawn for: /maps setspawn <orange/yellow>"));
		} else {
			String teamName = args[1];
			EggsTeam team;
			if (teamName.equalsIgnoreCase("orange")) {
				team = EggsTeam.ORANGE;
			} else if (teamName.equalsIgnoreCase("yellow")) {
				team = EggsTeam.YELLOW;
			} else {
				player.sendMessage(
						Eggs.formatMessage(ChatColor.RED + "The team must either be orange or yellow: " + teamName));
				return;
			}

			MapImpl map = MapImpl.get(player.getWorld());
			Location location = player.getLocation();
			if (map.isLocationPlotted(location)) {
				// Overwrite any previous spawns with the requested team
				for (LocationPair pair : map.getLocationPairs(location)) {
					if (isTeamSpawn(pair)) {
						pair.setKey(team.getSpawnKey()); // Replace it with this team
					}
				}
			} else {
				map.addLocation(location, team.getSpawnKey(), team.name());
			}

			Team apiTeam = team.getTeam();
			player.sendMessage(Eggs.formatMessage(
					ChatColor.GRAY + "Set current location to a " + apiTeam.getChatColor() + apiTeam.getTeamName() + " "
							+ ChatColor.GRAY
							+ "spawn point!"));
		}
	}

	private void deleteSpawn(Player player) {
		Location location = player.getLocation();
		MapImpl map = MapImpl.get(player.getWorld());
		if (map.isLocationPlotted(location)) {
			boolean removedOnce = false;
			for (LocationPair pair : map.getLocationPairs(location)) {
				if (isTeamSpawn(pair)) {
					map.removeLocation(pair);
					removedOnce = true;
				}
			}

			if (removedOnce) {
				player.sendMessage(Eggs.formatMessage(
						ChatColor.GRAY + "The spawn at your location has been " + ChatColor.RED + "deleted"));
			} else {
				player.sendMessage(Eggs.formatMessage(ChatColor.RED + "There is no spawn marked at your location"));
			}
		} else {
			player.sendMessage(Eggs.formatMessage(ChatColor.RED + "There is no spawn at your location!"));
		}
	}

	private boolean isTeamSpawn(LocationPair pair) {
		String key = pair.getKey();
		return key.equalsIgnoreCase(GameWorldConfig.ORANGE_SPAWNS_KEY) ||
				key.equalsIgnoreCase(GameWorldConfig.YELLOW_SPAWNS_KEY);
	}

	private void author(Player player, String[] args) {
		MapImpl map = MapImpl.get(player.getWorld());
		if (args.length < 3) {
			StringBuilder authorsBuilder = new StringBuilder();
			List<String> authors = map.getAuthors();
			if (authors.isEmpty()) {
				authorsBuilder.append(ChatColor.RED);
				authorsBuilder.append("None");
			} else {
				for (int i = 0; i < authors.size(); i++) {
					authorsBuilder.append(authors.get(i));
					if (i + 1 < authors.size()) {
						authorsBuilder.append(", ");
					}
				}
			}
			player.sendMessage("Current Authors: " + authorsBuilder.toString());
		} else {
			String addRemove = args[1];
			boolean add;
			if (addRemove.equalsIgnoreCase("add")) {
				add = true;
			} else if (addRemove.equalsIgnoreCase("remove")) {
				add = false;
			} else {
				player.sendMessage(
						Eggs.formatMessage(ChatColor.RED + "Neither add nor remove was specified: " + addRemove));
				return;
			}

			String username = args[2];
			if (add) {
				map.addAuthor(username);
				player.sendMessage(Eggs.formatMessage(
						ChatColor.GREEN + "Added " + ChatColor.GRAY + "the user " + ChatColor.YELLOW + username
								+ ChatColor.GRAY + " as a map author"));
			} else {
				if (map.getAuthors().contains(username)) {
					map.removeAuthor(username);
					player.sendMessage(Eggs.formatMessage(
							ChatColor.RED + "Removed " + ChatColor.GRAY + " the user " + ChatColor.YELLOW + username
									+ ChatColor.GRAY + " from being a map author"));
				} else {
					player.sendMessage(Eggs.formatMessage(ChatColor.RED
							+ "That user isn't currently an author, did you capitalize the name properly?"));
				}
			}
		}
	}

	private void setName(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(
					Eggs.formatMessage(ChatColor.RED + "Insufficient Arguments. Expected: /maps setname <name>"));
		} else {
			StringBuilder nameBuilder = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				nameBuilder.append(args[i]);
				nameBuilder.append(" ");
			}
			String name = nameBuilder.toString();

			MapImpl map = MapImpl.get(player.getWorld());
			map.setFullName(name);
			player.sendMessage(
					Eggs.formatMessage(ChatColor.GRAY + "Set the world name to " + ChatColor.GREEN + name));
		}
	}

	private void commit(Player player) {
		MapImpl map = MapImpl.get(player.getWorld());
		if (map.isValid()) {
			try {
				boolean success = map.commitToFile();
				if (success) {
					player.sendMessage(Eggs.formatMessage(
							ChatColor.GRAY + "The world has been " + ChatColor.GREEN + "successfully committed "
									+ ChatColor.GRAY + "to storage as an Eggs game map!"));
				} else {
					player.sendMessage(Eggs.formatMessage(
							ChatColor.RED + "An unexpected error occurred creating the data file!"));
				}
			} catch (FileAlreadyExistsException e) {
				e.printStackTrace();
				player.sendMessage(
						ChatColor.RED + "An internal storage error occurred: Unable to overwrite existing file");
			}
		} else {
			player.sendMessage(Eggs.formatMessage(
					ChatColor.RED + "Your world is not a valid game map, consult '/maps info' for more information"));
		}
	}

}
