package commons.data;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author vadim
 */
public interface AccountStorage<T extends Account> {

	T getAccount(OfflinePlayer player);

	T getAccount(UUID uuid);

	void flushAndSave();

	void savePlayer(OfflinePlayer player);

	void savePlayer(UUID uuid);

	void loadPlayer(OfflinePlayer player);

	void loadPlayer(UUID uuid);

}
