package generators.impl.data;

import commons.data.JDBCAccountStorage;

import java.util.UUID;

/**
 * todo: sql
 * @author vadim
 */
public class GenAccountStorage extends JDBCAccountStorage<GenAccount> {

	public GenAccountStorage() {
		super(GenAccount::new);
	}

	@Override
	public void flushAndSave() {
		/*SQLiteAdapter sql = plugin.newDbAdapter();
		sql.init();
		for (Account value : cache.values())
			sql.write(value);
		sql.close();

		List<UUID> online = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toList();
		new ArrayList<>(cache.keySet()).stream().filter(uid -> !online.contains(uid)).forEach(cache::remove); // new ArrayList<>() to avoid ConcurrentModificationException*/
	}


	@Override
	public void savePlayer(UUID uuid) {
				/*SQLiteAdapter sql = plugin.newDbAdapter();
		sql.init();
		sql.write(data);
		sql.close();*/
	}

	@Override
	public void loadPlayer(UUID uuid) {
		/*SQLiteAdapter sql = plugin.newDbAdapter();
		sql.init();
		sql.read(data);
		sql.close();*/
	}

}
