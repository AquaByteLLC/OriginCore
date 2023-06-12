package commons.impl;

import commons.Owned;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author vadim
 */
public abstract class PlayerOwned implements Owned {

	private final UUID uuid;

	public PlayerOwned(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public OfflinePlayer getOfflineOwner() { return Bukkit.getOfflinePlayer(uuid); }

	@Override
	public UUID getOwnerUUID() { return uuid; }

}
