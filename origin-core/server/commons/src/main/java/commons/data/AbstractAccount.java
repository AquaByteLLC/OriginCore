package commons.data;

import com.j256.ormlite.field.DatabaseField;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author vadim
 */
public abstract class AbstractAccount implements Account {

	@DatabaseField(id = true)
	private final UUID uuid;

	public AbstractAccount(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public OfflinePlayer getOwner() {
		return Bukkit.getOfflinePlayer(uuid);
	}

}
