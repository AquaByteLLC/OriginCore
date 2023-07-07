package commons.data;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author vadim
 */
public interface Owned {

	OfflinePlayer getOfflineOwner();

	UUID getOwnerUUID();

	boolean isOwnedBy(OfflinePlayer test);

	boolean isOwnedBy(UUID test);

}