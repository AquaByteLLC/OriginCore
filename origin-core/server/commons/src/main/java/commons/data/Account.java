package commons.data;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author vadim
 */
public interface Account {

	UUID getUUID();

	OfflinePlayer getOwner();

}
