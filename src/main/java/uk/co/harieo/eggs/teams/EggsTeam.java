package uk.co.harieo.eggs.teams;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.md_5.bungee.api.ChatColor;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.config.GameWorldConfig;
import uk.co.harieo.eggs.stages.GameEndStage;
import uk.co.harieo.minigames.scoreboards.tablist.modules.Affix;
import uk.co.harieo.minigames.teams.ColourGroup;
import uk.co.harieo.minigames.teams.PlayerBasedTeam;

public enum EggsTeam {

	// This is an enum to constrain the handler to only the provided teams and no others
	ORANGE("Orange", ColourGroup.GOLD, GameWorldConfig.ORANGE_SPAWNS_KEY),
	YELLOW("Yellow", ColourGroup.YELLOW, GameWorldConfig.YELLOW_SPAWNS_KEY);

	private final PlayerBasedTeam team;

	private int score = 0;
	private final String spawnKey;
	private final ItemStack leatherChestplate;
	private final Affix affix;

	EggsTeam(String name, ColourGroup colourGroup, String spawnKey) {
		this.team = new PlayerBasedTeam(name, colourGroup);
		this.spawnKey = spawnKey;

		this.leatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta meta = (LeatherArmorMeta) leatherChestplate.getItemMeta();
		if (meta != null) {
			meta.setColor(colourGroup.getEquipmentColor());
			leatherChestplate.setItemMeta(meta);
		}

		this.affix = new Affix(name()).setPrefix(colourGroup.getChatColor() + name + ChatColor.RESET + " ");
	}

	public PlayerBasedTeam getTeam() {
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

	public ItemStack getLeatherChestplate() {
		return leatherChestplate;
	}

	public void setChestplate(Player player) {
		player.getInventory().setChestplate(getLeatherChestplate());
	}

	public Affix getAffix() {
		return affix;
	}

	public ColourGroup getColourGroup() {
		return team.getColour();
	}

	public ChatColor getChatColor() {
		return getColourGroup().getChatColor().asBungee();
	}

	public static EggsTeam assignTeam(Player player) {
		// Adds the player to the team with the least players to auto-balance them over time
		if (ORANGE.getTeam().getMembers().size() >= YELLOW.getTeam().getMembers().size()) {
			setTeam(player, YELLOW);
			return YELLOW;
		} else {
			setTeam(player, ORANGE);
			return ORANGE;
		}
	}

	public static void setTeam(Player player, EggsTeam team) {
		Eggs.getInstance().getTeamHandler().setTeam(player, team.getTeam());
		updatePlayersTeam(player, team);
	}

	public static EggsTeam getTeam(Player player) {
		if (ORANGE.getTeam().isMember(player)) {
			return ORANGE;
		} else if (YELLOW.getTeam().isMember(player)) {
			return YELLOW;
		} else {
			return null;
		}
	}

	private static EggsTeam getTeam(PlayerBasedTeam genericTeam) {
		for (EggsTeam eggsTeam : values()) {
			if (eggsTeam.getTeam().equals(genericTeam)) {
				return eggsTeam;
			}
		}
		return null;
	}

	public static void updatePlayersTeam(Player player, EggsTeam team) {
		team.setChestplate(player);
		player.setDisplayName(team.getColourGroup().getChatColor() + player.getName());
		Eggs.updateTabListFactories();
	}

	public static boolean isInTeam(Player player) {
		return ORANGE.getTeam().isMember(player) || YELLOW.getTeam().isMember(player);
	}

}
