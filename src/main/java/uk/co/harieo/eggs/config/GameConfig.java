package uk.co.harieo.eggs.config;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

public class GameConfig {

	private int minutesOfGame = 5;
	private World lobbyWorld;
	private GameWorldConfig gameWorldConfig;

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
		lobbyWorld = parseWorld(config, "lobby-world");
		gameWorldConfig = new GameWorldConfig(parseWorld(config, "game-world"));
	}

	private World parseWorld(FileConfiguration config, String key) {
		String lobbyWorldName = Objects
				.requireNonNull(config.getString(key), "Missing " + key + " value in config.yml");
		return Bukkit.getWorld(lobbyWorldName);
	}

	public int getMinutesOfGame() {
		return minutesOfGame;
	}

	public World getLobbyWorld() {
		return lobbyWorld;
	}

	public GameWorldConfig getGameWorldConfig() {
		return gameWorldConfig;
	}

}
