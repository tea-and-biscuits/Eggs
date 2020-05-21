package uk.co.harieo.eggs.teams;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import uk.co.harieo.minigames.scoreboards.tablist.modules.Affix;
import uk.co.harieo.minigames.scoreboards.tablist.modules.TabListProcessor;

public class TabTeamProcessor extends TabListProcessor {

	private static final List<Affix> affixes = new ArrayList<>();

	static {
		for (EggsTeam team : EggsTeam.values()) {
			affixes.add(team.getAffix());
		}
	}

	@Override
	protected List<Affix> getInitialAffixes() {
		return affixes;
	}

	@Override
	public Optional<Affix> getAffixForPlayer(Player player) {
		EggsTeam team = EggsTeam.getTeam(player);
		if (team != null) {
			return Optional.of(team.getAffix());
		} else {
			return Optional.empty();
		}
	}

}
