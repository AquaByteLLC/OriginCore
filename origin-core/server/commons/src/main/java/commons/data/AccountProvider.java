package commons.data;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author vadim
 */
public interface AccountProvider<T extends Account> {

	T getAccount(OfflinePlayer player);

	T getAccount(UUID uuid);

}
