package uk.co.harieo.eggs.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;

import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.players.CoinsHandler;
import uk.co.harieo.eggs.stages.GameStartStage;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.games.GameStage;

public class CombatListener implements Listener {

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (Eggs.getInstance().getGameStage() != GameStage.IN_GAME) {
			return;
		}

		Projectile projectile = event.getEntity();
		Entity entity = event.getHitEntity();
		if (projectile.getType() == EntityType.EGG && entity instanceof Player) {
			Player target = (Player) entity;

			boolean willDie = target.getHealth() <= 6;
			target.damage(6); // 3 hearts
			if (willDie) {
				ProjectileSource source = projectile.getShooter();
				if (source instanceof Player) {
					Player shooter = (Player) source;
					EggsTeam team = EggsTeam.getTeam(shooter);
					if (team != null) {
						shooter.sendMessage(Eggs.formatMessage(
								ChatColor.GRAY + "You have killed " + ChatColor.YELLOW + target.getName()
										+ ChatColor.GREEN
										+ " for " + ChatColor.GREEN + "+10 Coins"));
						CoinsHandler.addCoins(shooter, 10);
						team.setScore(team.getScore() + 1);
						shooter.playSound(shooter.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 0.5F);
					} else {
						shooter.sendMessage(ChatColor.RED + "You are not on a team. This is a fatal error!");
						Eggs.getInstance().getLogger().warning(
								shooter.getName() + " is playing without a team. This should not be possible.");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		event.setKeepInventory(true);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		EggsTeam team = EggsTeam.getTeam(player);
		if (team != null) {
			event.setRespawnLocation(GameStartStage.getSpawnLocation(team));
		}
	}

	@EventHandler
	public void onEntityCreation(EntitySpawnEvent event) {
		if (event.getEntityType() == EntityType.CHICKEN) {
			event.setCancelled(true); // Stop chickens spawning from the eggs
		}
	}

}
