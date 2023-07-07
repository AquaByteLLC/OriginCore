package commons.data.redis.impl;

import commons.data.redis.RedisHandler;

import java.util.UUID;

public class GemsProfile implements RedisHandler.RedisData {
	private final UUID uuid;
	private final int currentBal; // I KNOW ITS AN INT STFU ITS JUST TO TEST


	public GemsProfile(UUID uuid, int currentBal) {
		this.uuid = uuid;
		this.currentBal = currentBal;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getCurrentBal() {
		return currentBal;
	}
}
