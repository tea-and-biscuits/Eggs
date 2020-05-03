package uk.co.harieo.eggs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.purchasables.CoinsHandler;
import uk.co.harieo.eggs.stages.GameStartStage;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.games.GameStage;

public class CombatListener implements Listener {

	private static final Map<UUID, Double> damageMap = new HashMap<>();

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (Eggs.getInstance().getGameStage() != GameStage.IN_GAME) {
			return;
		}

		Projectile projectile = event.getEntity();
		Entity entity = event.getHitEntity();
		if (projectile.getType() == EntityType.EGG && entity instanceof Player) {
			Player target = (Player) entity;
			ProjectileSource source = projectile.getShooter();
			if (source instanceof Player) {
				Player shooter = (Player) source;
				EggsTeam team = EggsTeam.getTeam(shooter);
				EggsTeam targetTeam = EggsTeam.getTeam(target);

				if (team == null || targetTeam == null || team == targetTeam) {
					return; // Friendly fire
				}

				target.damage(getDamage(shooter));
			}
		}
	}

	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		Entity damagingEntity = event.getDamager();
		Entity victimEntity = event.getEntity();
		if (damagingEntity instanceof Egg && victimEntity instanceof Player) {
			Player target = (Player) victimEntity;

			Egg egg = (Egg) damagingEntity;
			ProjectileSource source = egg.getShooter();
			if (source instanceof Player) {
				Player shooter = (Player) source;

				EggsTeam team = EggsTeam.getTeam(shooter);
				EggsTeam targetTeam = EggsTeam.getTeam(target);

				boolean isSameTeam = targetTeam != null && team == targetTeam;
				if (getDamage(shooter) < target.getHealth() || team == null || isSameTeam) {
					return; // Not a kill or team doesn't exist
				}

				shooter.sendMessage(Eggs.formatMessage(
						ChatColor.GRAY + "You have killed " + target.getDisplayName()
								+ ChatColor.GRAY
								+ " for " + ChatColor.GREEN + "+10 Coins"));
				broadcastWithExclusion(shooter,
						shooter.getDisplayName() + ChatColor.GRAY + " has splat " + target.getDisplayName()
								+ ChatColor.GRAY + " to death!");
				CoinsHandler.addCoins(shooter, 10);
				team.setScore(team.getScore() + 1);
				shooter.playSound(shooter.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 0.5F);
			}
		} else if (damagingEntity instanceof Chicken && victimEntity instanceof Player) {
			Chicken chicken = (Chicken) damagingEntity;
			UUID uuid = chicken.getBreedCause(); // This is set in the QuackAttackRunnable to be used here
			if (uuid != null) {
				Player summoner = Bukkit.getPlayer(uuid);
				Player victim = (Player) victimEntity;
				if (summoner != null) {
					EggsTeam summonerTeam = EggsTeam.getTeam(summoner);
					EggsTeam victimTeam = EggsTeam.getTeam(victim);
					if (summonerTeam != null && victimTeam != null && summonerTeam != victimTeam) {
						event.setDamage(getDamage(summoner));
						summoner.sendMessage(
								ChatColor.YELLOW + ChatColor.BOLD.toString() + "Chicken " + chicken.getCustomName()
										+ ChatColor.GRAY + " has exploded " + victim.getDisplayName());
						broadcastWithExclusion(summoner,
								summoner.getDisplayName() + ChatColor.GRAY + " has exploded " + victim.getDisplayName()
										+ ChatColor.GRAY + " with a " + ChatColor.GREEN + "Quack Attack "
										+ ChatColor.GRAY + "chicken!");
						return;
					}
				}
			}

			event.setCancelled(true);
		} else {
			event.setCancelled(true);
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
	public void onEntityCreation(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.EGG) {
			event.setCancelled(true); // Stop chickens spawning from the eggs
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		event.getDrops().clear();
	}

	@EventHandler
	public void onRegeneration(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
		}
	}

	private void broadcastWithExclusion(Player player, String message) {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (onlinePlayer != player) {
				onlinePlayer.sendMessage(Eggs.formatMessage(message));
			}
		}
	}

	public static double getDamage(Player player) {
		return damageMap.getOrDefault(player.getUniqueId(), 2.0);
	}

	public static void setDamage(Player player, double damage) {
		resetDamage(player);
		damageMap.put(player.getUniqueId(), damage);
	}

	public static void resetDamage(Player player) {
		damageMap.remove(player.getUniqueId());
	}

}
