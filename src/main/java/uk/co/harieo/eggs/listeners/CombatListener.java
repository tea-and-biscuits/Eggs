package uk.co.harieo.eggs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;
import uk.co.harieo.eggs.Eggs;
import uk.co.harieo.eggs.purchasables.CoinsHandler;
import uk.co.harieo.eggs.stages.GameStartStage;
import uk.co.harieo.eggs.teams.EggsTeam;
import uk.co.harieo.minigames.games.GameStage;

public class CombatListener implements Listener {

	private static final Map<UUID, Double> damageMap = new HashMap<>();
	private static final List<UUID> noCoins = new ArrayList<>();
	private static final Set<UUID> damageBuffer = new HashSet<>();

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

				if (team == null || targetTeam == null || team == targetTeam
						|| damageBuffer.contains(target.getUniqueId())) {
					return; // Friendly fire
				}

				double damage = getDamage(shooter);
				if (handleShooting(shooter, target, damage)) {
					target.damage(damage);
				}
			}
		}
	}

	public boolean handleShooting(Player damagingEntity, Player victimEntity, double damage) {
		if (Eggs.getInstance().getGameStage() != GameStage.IN_GAME) {
			return false;
		}

		EggsTeam shooterTeam = EggsTeam.getTeam(damagingEntity);
		EggsTeam targetTeam = EggsTeam.getTeam(victimEntity);

		boolean isSameTeam = targetTeam != null && shooterTeam == targetTeam;
		if (damage < victimEntity.getHealth() || shooterTeam == null || isSameTeam) {
			return true; // Not a kill or team doesn't exist
		}

		boolean earningCoins = !noCoins.contains(damagingEntity.getUniqueId());
		damagingEntity.sendMessage(Eggs.formatMessage(
				ChatColor.GRAY + "You have killed " + victimEntity.getDisplayName()
						+ (earningCoins ? ChatColor.GRAY + " for " + ChatColor.GREEN + "+10 Coins" : "")));
		broadcastWithExclusion(damagingEntity,
				shooterTeam.getTeam().getColour().getChatColor() + damagingEntity.getName()
						+ ChatColor.GRAY + " has splat "
						+ (targetTeam != null ? targetTeam.getTeam().getColour().getChatColor() : ChatColor.YELLOW)
						+ victimEntity.getName()
						+ ChatColor.GRAY + " to death!");
		if (earningCoins) {
			CoinsHandler.addCoins(damagingEntity, 10);
		}
		shooterTeam.setScore(shooterTeam.getScore() + 1);
		damagingEntity.playSound(damagingEntity.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 0.5F);

		simulateDeath(victimEntity);
		return false;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Entity damagingEntity = event.getDamager();
		Entity victimEntity = event.getEntity();
		if (damagingEntity instanceof Chicken && victimEntity instanceof Player) {
			Chicken chicken = (Chicken) damagingEntity;
			UUID uuid = chicken.getBreedCause(); // This is set in the QuackAttackRunnable to be used here
			if (uuid != null) {
				Player summoner = Bukkit.getPlayer(uuid);
				Player victim = (Player) victimEntity;
				if (summoner != null) {
					EggsTeam summonerTeam = EggsTeam.getTeam(summoner);
					EggsTeam victimTeam = EggsTeam.getTeam(victim);
					if (summonerTeam != null && victimTeam != null && summonerTeam != victimTeam) {
						simulateDeath(victim);
						summoner.sendMessage(
								Eggs.formatMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Chicken " + chicken
										.getCustomName()
										+ ChatColor.GRAY + " has exploded " + victim.getDisplayName()));
						broadcastWithExclusion(summoner,
								summoner.getDisplayName() + ChatColor.GRAY + " has exploded " + victim.getDisplayName()
										+ ChatColor.GRAY + " with a " + ChatColor.GREEN + "Quack Attack "
										+ ChatColor.GRAY + "chicken!");
						summonerTeam.setScore(summonerTeam.getScore() + 1);
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
		simulateDeath(event.getEntity());
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

	private void broadcastWithExclusion(Player player, String message) {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (onlinePlayer != player) {
				onlinePlayer.sendMessage(Eggs.formatMessage(message));
			}
		}
	}

	private void simulateDeath(Player player) {
		damageBuffer.add(player.getUniqueId());
		player.setHealth(4);
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_DEATH, 0.5F, 0.5F);
		EggsTeam team = EggsTeam.getTeam(player);
		if (team != null) {
			player.teleport(GameStartStage.getSpawnLocation(team));
		}
		Bukkit.getScheduler().runTaskLater(Eggs.getInstance(), () -> damageBuffer.remove(player.getUniqueId()), 20 * 3);
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

	public static void disableCoins(Player player) {
		noCoins.add(player.getUniqueId());
	}

	public static void enableCoins(Player player) {
		noCoins.remove(player.getUniqueId());
	}

}
