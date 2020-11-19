package uk.co.harieo.eggs.config;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import uk.co.harieo.eggs.Eggs;

public class GameConfig {

	private int minutesOfGame = 5;
	private World lobbyWorld;
	private GameWorldConfig gameWorldConfig;
	private List<String> timerMessages;

	public GameConfig(JavaPlugin plugin) {
		try {
			File configFile = getConfigFile(plugin);
			FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
			parseConfig(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File getConfigFile(JavaPlugin plugin) throws IOException {
		File dataFolder = plugin.getDataFolder();
		if (!dataFolder.exists()) {
			if (!dataFolder.mkdir()) {
				throw new IOException("Failed to create data folder");
			}
		}

		File file = new File(dataFolder, "config.yml");
		if (!file.exists()) {
			try (InputStream stream = Objects
					.requireNonNull(plugin.getResource("config.yml"), "Hard resource missing")) {
				Files.copy(stream, file.toPath());
			}
		}

		return file;
	}

	private void parseConfig(FileConfiguration config) {
		minutesOfGame = config.getInt("game-time");
		timerMessages = config.getStringList("timer-messages");
		lobbyWorld = parseLobbyWorld(config);

		List<String> worldNames = config.getStringList("game-worlds");
		String worldName = worldNames.get(Eggs.RANDOM.nextInt(worldNames.size()));

		World world = Bukkit.getWorld(worldName);
		if (world == null) { // Probably not loaded
			WorldCreator creator = new WorldCreator(worldName);
			world = Bukkit.createWorld(creator);
		}

		gameWorldConfig = new GameWorldConfig(world);
	}

	private World parseLobbyWorld(FileConfiguration config) {
		String lobbyWorldName = Objects
				.requireNonNull(config.getString("lobby-world"), "Missing lobby-world value in config.yml");
		return Bukkit.getWorld(lobbyWorldName);
	}

	public int getMinutesOfGame() {
		return minutesOfGame;
	}

	public List<String> getTimerMessages() {
		return timerMessages;
	}

	public World getLobbyWorld() {
		return lobbyWorld;
	}

	public GameWorldConfig getGameWorldConfig() {
		return gameWorldConfig;
	}

}
