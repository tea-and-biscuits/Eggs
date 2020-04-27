package uk.co.harieo.eggs.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import uk.co.harieo.eggs.stages.GameStartStage;
import uk.co.harieo.minigames.scoreboards.elements.RenderableElement;
import uk.co.harieo.minigames.timing.GameTimer;

public class TimeLeftElement implements RenderableElement {

	@Override
	public String getText(Player player) {
		GameTimer timer = GameStartStage.gameTimer;
		if (timer.isCancelled()) {
			return "Waiting...";
		} else {
			int timeRemaining = timer.getSecondsLeft();
			int minutes = timeRemaining / 60;
			int seconds = timeRemaining % 60;
			if (seconds == 0) {
				return minutes + " minutes";
			} else {
				return ChatColor.WHITE.toString() + minutes + " minutes, " + seconds + " seconds";
			}
		}
	}

}
