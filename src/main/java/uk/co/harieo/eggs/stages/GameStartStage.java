package uk.co.harieo.eggs.stages;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.scoreboard.TeamElement;
import uk.co.harieo.eggs.scoreboard.TeamScoreElement;
import uk.co.harieo.minigames.scoreboards.GameBoard;
import uk.co.harieo.minigames.scoreboards.elements.ConstantElement;

public class GameStartStage {

	private static final GameBoard gameBoard = new GameBoard(ChatColor.GREEN + ChatColor.BOLD.toString() + "Eggs",
			DisplaySlot.SIDEBAR);

	static {
		gameBoard.addBlankLine();
		gameBoard.addLine(new ConstantElement(ChatColor.YELLOW + "Time Left"));
		// TODO
		gameBoard.addBlankLine();
		gameBoard.addLine(new TeamElement());
		gameBoard.addLine(new TeamScoreElement());
		gameBoard.addBlankLine();
		gameBoard.addLine(Eggs.IP_ELEMENT);
	}

	public static void renderScoreboard(Player player) {
		gameBoard.render(Eggs.getInstance(), player, 20);
	}

}
