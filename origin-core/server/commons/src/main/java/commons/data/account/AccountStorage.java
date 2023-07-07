package commons.data.account;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author vadim
 */
public interface AccountStorage<T extends Account> extends AccountProvider<T> {

	Class<T> getAccountClass();

	void flushAndSave();

	void savePlayer(OfflinePlayer player);

	void savePlayer(UUID uuid);

	void loadPlayer(OfflinePlayer player);

	void loadPlayer(UUID uuid);

}
