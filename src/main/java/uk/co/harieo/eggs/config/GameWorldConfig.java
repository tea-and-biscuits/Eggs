package uk.co.harieo.eggs.config;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.minigames.maps.LocationPair;
import uk.co.harieo.minigames.maps.MapImpl;

public class GameWorldConfig {

	public static final String ORANGE_SPAWNS_KEY = "orange-spawn";
	public static final String YELLOW_SPAWNS_KEY = "yellow-spawn";

	private MapImpl map;
	private final List<Location> orangeSpawns = new ArrayList<>();
	private final List<Location> yellowSpawns = new ArrayList<>();

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

}
