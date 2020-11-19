package uk.co.harieo.eggs.config;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;
import uk.co.harieo.minigames.utils.Cuboid;

public class GameWorldConfig {

	public static final String ORANGE_SPAWNS_KEY = "orange-spawn";
	public static final String YELLOW_SPAWNS_KEY = "yellow-spawn";
	public static final String WALL_CORNER_KEY = "spawn-wall-corner";

	private MapImpl map;
	private final List<Location> orangeSpawns = new ArrayList<>();
	private final List<Location> yellowSpawns = new ArrayList<>();
	private Cuboid orangeWall;
	private Cuboid yellowWall;

	private boolean loaded = false;

	public GameWorldConfig(World gameWorld) {
		Logger logger = Eggs.getInstance().getLogger();
		if (gameWorld == null) {
			logger.severe("Failed to load game world");
		} else {
			try {
				map = MapImpl.parseWorld(gameWorld);
				List<LocationPair> orangePairs = map.getLocationsByKey(ORANGE_SPAWNS_KEY);
				if (orangePairs.isEmpty()) {
					logger.severe("There are no spawns for the orange team...");
				} else {
					for (LocationPair pair : orangePairs) {
						orangeSpawns.add(pair.getLocation());
					}
					logger.info("Loaded " + orangeSpawns.size() + " spawn locations for orange team");
				}

				List<LocationPair> yellowPairs = map.getLocationsByKey(YELLOW_SPAWNS_KEY);
				if (yellowPairs.isEmpty()) {
					logger.severe("There are no spawns for the yellow team...");
				} else {
					for (LocationPair pair : yellowPairs) {
						yellowSpawns.add(pair.getLocation());
					}
					logger.info("Loaded " + yellowSpawns.size() + " spawn locations for yellow team");
				}

				List<LocationPair> wallCorners = map.getLocationsByKey(WALL_CORNER_KEY);
				List<LocationPair> orangeWallCorners = wallCorners.stream()
						.filter(pair -> pair.getValue().equalsIgnoreCase(EggsTeam.ORANGE.name()))
						.collect(Collectors.toList());
				List<LocationPair> yellowWallCorners = wallCorners.stream()
						.filter(pair -> pair.getValue().equalsIgnoreCase(EggsTeam.YELLOW.name()))
						.collect(Collectors.toList());

				if (orangeWallCorners.size() < 2) {
					logger.warning("There aren't enough corners to make an orange team spawn wall!");
				} else {
					orangeWall = new Cuboid(orangeWallCorners.get(0).getLocation(),
							orangeWallCorners.get(1).getLocation());
					createSpawnWall(orangeWall, EggsTeam.ORANGE);
				}

				if (yellowWallCorners.size() < 2) {
					logger.warning("There aren't enough corners to make a yellow team spawn wall!");
				} else {
					yellowWall = new Cuboid(yellowWallCorners.get(0).getLocation(),
							yellowWallCorners.get(1).getLocation());
					createSpawnWall(yellowWall, EggsTeam.YELLOW);
				}

				loaded = true;
			} catch (FileNotFoundException ignored) {
				logger.severe("The game world has not been properly formatted");
			}
		}
	}

	public MapImpl getMap() {
		return map;
	}

	public List<Location> getOrangeSpawns() {
		return orangeSpawns;
	}

	public List<Location> getYellowSpawns() {
		return yellowSpawns;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void deleteSpawnWalls() {
		deleteSpawnWall(orangeWall, EggsTeam.ORANGE);
		deleteSpawnWall(yellowWall, EggsTeam.YELLOW);
	}

	public static void createSpawnWall(Cuboid wall, EggsTeam team) {
		wall.forEach(block -> {
			if (block.isEmpty()) {
				block.setType(team.getColourGroup().getGlassType());
			}
		});
	}

	public static void deleteSpawnWall(Cuboid wall, EggsTeam team) {
		wall.forEach(block -> {
			if (block.getType() == team.getColourGroup().getGlassType()) {
				block.setType(Material.AIR);
			}
		});
	}

}
