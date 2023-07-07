package commons.data.account.impl;

import com.j256.ormlite.dao.Dao;
import commons.data.account.Account;
import commons.data.account.AccountFactory;
import commons.data.account.AccountStorage;
import commons.data.sql.DatabaseSession;
import commons.data.sql.SessionProvider;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public abstract class ORMLiteAccountStorage<T extends Account> implements AccountStorage<T> {

	private final Map<UUID, T> cache = new HashMap<>(500);

	protected final AccountFactory<T> factory;
	protected final SessionProvider provider;
	protected final Class<T> clazz;

	public ORMLiteAccountStorage(AccountFactory<T> factory, SessionProvider provider, Class<T> clazz) {
		this.factory = factory;
		this.provider = provider;
		this.clazz = clazz;
	}

	@Override
	public Class<T> getAccountClass() {
		return clazz;
	}

	@Override
	@SneakyThrows
	public final void flushAndSave() {
		try (DatabaseSession sesh = provider.session()) {
			Set<UUID> online = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
			Dao<T, UUID> dao = sesh.getDAO(clazz, UUID.class);

			UUID uuid;
			Iterator<UUID> iter = cache.keySet().iterator();
			while (iter.hasNext()) {
				uuid = iter.next();
				save(cache.get(uuid), dao);

				if (!online.contains(uuid))
					iter.remove();
			}
		}
	}

	@Override
	public T getAccount(OfflinePlayer player) {
		return getAccount(player.getUniqueId());
	}

	@Override
	public T getAccount(UUID uuid) {
		return cache.computeIfAbsent(uuid, factory::create);
	}

	protected abstract void save(T account, Dao<T, UUID> dao) throws SQLException;

	protected abstract T load(UUID uuid, Dao<T, UUID> dao) throws SQLException;

	@Override
	public final void savePlayer(OfflinePlayer player) {
		savePlayer(player.getUniqueId());
	}

	@Override
	@SneakyThrows
	public final void savePlayer(UUID uuid) {
		try (DatabaseSession sesh = provider.session()) {
			Dao<T, UUID> dao = sesh.getDAO(clazz, UUID.class);
			save(getAccount(uuid), dao);
		}
	}

	@Override
	public final void loadPlayer(OfflinePlayer player) {
		loadPlayer(player.getUniqueId());
	}

	@Override
	@SneakyThrows
	public final void loadPlayer(UUID uuid) {
		try (DatabaseSession sesh = provider.session()) {
			Dao<T, UUID> dao = sesh.getDAO(clazz, UUID.class);
			cache.put(uuid, load(uuid, dao));
		}
	}

}