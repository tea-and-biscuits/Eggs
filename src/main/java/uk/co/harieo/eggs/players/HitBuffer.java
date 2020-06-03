package uk.co.harieo.eggs.players;

import org.bukkit.entity.Player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HitBuffer {

	private static final Cache<UUID, AtomicInteger> hitsPerSecond = CacheBuilder.newBuilder()
			.expireAfterWrite(1, TimeUnit.SECONDS).build();

	public static void addHit(Player player) {
		UUID uuid = player.getUniqueId();
		AtomicInteger hits = hitsPerSecond.getIfPresent(uuid);
		if (hits != null) {
			hits.set(hits.intValue() + 1);
		} else {
			hitsPerSecond.put(uuid, new AtomicInteger(1));
		}
	}

	public static int getHitsInLastSecond(Player player) {
		AtomicInteger rawHits = hitsPerSecond.getIfPresent(player.getUniqueId());
		return rawHits != null ? rawHits.get() : 0;
	}

}
