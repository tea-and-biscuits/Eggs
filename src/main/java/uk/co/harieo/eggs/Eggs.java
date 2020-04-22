package uk.co.harieo.eggs;

import org.bukkit.plugin.java.JavaPlugin;

import uk.co.harieo.minigames.games.Minigame;

public class Eggs extends JavaPlugin implements Minigame {

	@Override
	public void onEnable() {

	}

	@Override
	public String getMinigameName() {
		return "Eggs";
	}

	@Override
	public int getMaxPlayers() {
		return 10;
	}

	@Override
	public int getOptimalPlayers() {
		return 6;
	}

}
