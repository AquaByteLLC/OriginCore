package commons;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * todo: move to core
 * @author vadim
 */
public interface Owned {

	OfflinePlayer getOfflineOwner();

	UUID getOwnerUUID();

}