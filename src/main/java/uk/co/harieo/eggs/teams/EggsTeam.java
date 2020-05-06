package uk.co.harieo.eggs.teams;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.eggs.config.GameWorldConfig;
import uk.co.harieo.eggs.stages.GameEndStage;
import uk.co.harieo.minigames.teams.Team;

public enum EggsTeam {

	// This is an enum to constrain the handler to only the provided teams and no others
	ORANGE(new Team("Orange", ChatColor.GOLD, Color.ORANGE, 5), GameWorldConfig.ORANGE_SPAWNS_KEY,
			Material.ORANGE_STAINED_GLASS),
	YELLOW(new Team("Yellow", ChatColor.YELLOW, Color.YELLOW, 5), GameWorldConfig.YELLOW_SPAWNS_KEY,
			Material.YELLOW_STAINED_GLASS);

	private final Team team;
	private int score = 0;
	private final String spawnKey;
	private final Material glassMaterial;
	private final ItemStack leatherChestplate;

	EggsTeam(Team team, String spawnKey, Material glassMaterial) {
		this.team = team;
		this.spawnKey = spawnKey;
		this.glassMaterial = glassMaterial;

		this.leatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta meta = (LeatherArmorMeta) leatherChestplate.getItemMeta();
		if (meta != null) {
			meta.setColor(team.getArmorColor());
			leatherChestplate.setItemMeta(meta);
		}
	}

	public Team getTeam() {
		return team;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
		if (GameEndStage.hasTeamWon(this)) {
			GameEndStage.declareWinner(this);
		}
	}

	public String getSpawnKey() {
		return spawnKey;
	}

	public Material getGlassMaterial() {
		return glassMaterial;
	}

	public ItemStack getLeatherChestplate() {
		return leatherChestplate;
	}

	public void setChestplate(Player player) {
		player.getInventory().setChestplate(getLeatherChestplate());
	}

	public static EggsTeam assignTeam(Player player) {
		// Adds the player to the team with the least players to auto-balance them over time
		if (ORANGE.getTeam().countMembers() >= YELLOW.getTeam().countMembers()) {
			setTeam(player, YELLOW);
			return YELLOW;
		} else {
			setTeam(player, ORANGE);
			return ORANGE;
		}
	}

	public static EggsTeam getTeam(Player player) {
		if (ORANGE.getTeam().isTeamMember(player)) {
			return ORANGE;
		} else if (YELLOW.getTeam().isTeamMember(player)) {
			return YELLOW;
		} else {
			return null;
		}
	}

	public static void setTeam(Player player, EggsTeam team) {
		if (ORANGE.getTeam().isTeamMember(player)) {
			ORANGE.getTeam().removeTeamMember(player);
		}
		if (YELLOW.getTeam().isTeamMember(player)) {
			YELLOW.getTeam().removeTeamMember(player);
		}
		team.getTeam().addTeamMember(player);
		team.setChestplate(player);
		player.setDisplayName(team.getTeam().getChatColor() + player.getName());
	}

	public static boolean isInTeam(Player player) {
		return ORANGE.getTeam().isTeamMember(player) || YELLOW.getTeam().isTeamMember(player);
	}

}
