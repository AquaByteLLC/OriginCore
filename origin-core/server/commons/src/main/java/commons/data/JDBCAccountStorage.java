package commons.data;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author vadim
 */
public abstract class JDBCAccountStorage<T extends Account> implements AccountStorage<T> {

	//todo: ORMLite or JDBC impl and then polish and relocate into this class for re-use

	private final Map<UUID, T> cache = new HashMap<>(500);
	private final AccountFactory<T> factory;

	public JDBCAccountStorage(AccountFactory<T> factory) {
		this.factory = factory;
	}

	@Override
	public T getAccount(OfflinePlayer player) {
		return getAccount(player.getUniqueId());
	}

	@Override
	public T getAccount(UUID uuid) {
		return cache.computeIfAbsent(uuid, factory::create);
	}

	@Override
	public void savePlayer(OfflinePlayer player) {
		savePlayer(player.getUniqueId());
	}

	@Override
	public void loadPlayer(OfflinePlayer player) {
		loadPlayer(player.getUniqueId());
	}

}