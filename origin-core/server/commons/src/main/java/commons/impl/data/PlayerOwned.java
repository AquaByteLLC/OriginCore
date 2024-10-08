package commons.impl.data;

import com.j256.ormlite.field.DatabaseField;
import commons.data.Owned;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author vadim
 */
public abstract class PlayerOwned implements Owned {

	public static final String uuid_COLUMN = "owner_uuid";

	@DatabaseField(columnName = uuid_COLUMN)
	private final UUID uuid;

	public PlayerOwned(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public OfflinePlayer getOfflineOwner() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	@Override
	public UUID getOwnerUUID() {
		return uuid;
	}

	@Override
	public boolean isOwnedBy(OfflinePlayer test) {
		return isOwnedBy(test == null ? null : test.getUniqueId());
	}

	@Override
	public boolean isOwnedBy(UUID test) {
		return uuid.equals(test);
	}

}
