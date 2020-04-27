package uk.co.harieo.eggs.commands;

import org.bukkit.Location;
import org.bukkit.World;

import uk.co.harieo.eggs.commands.SpawnCuboidSubcommand.Position;
import uk.co.harieo.eggs.teams.EggsTeam;

public class PreCuboid {

	private final EggsTeam team;
	private final World world;

	private Position editingPosition;
	private Location corner1;
	private Location corner2;

	public PreCuboid(EggsTeam team, World world) {
		this.team = team;
		this.world = world;
	}

	public EggsTeam getTeam() {
		return team;
	}

	public Location getCorner1() {
		return corner1;
	}

	public void setCorner1(Location corner1) {
		this.corner1 = corner1;
	}

	public Location getCorner2() {
		return corner2;
	}

	public void setCorner2(Location corner2) {
		this.corner2 = corner2;
	}

	public Position getEditingPosition() {
		return editingPosition;
	}

	public void setEditingPosition(Position editingPosition) {
		this.editingPosition = editingPosition;
	}

	public World getWorld() {
		return world;
	}

}
