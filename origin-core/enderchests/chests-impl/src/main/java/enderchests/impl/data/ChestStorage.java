package enderchests.impl.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import commons.data.DatabaseSession;
import commons.data.SessionProvider;
import enderchests.ChestRegistry;
import enderchests.impl.LinkedEnderChest;
import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.UUID;

/**
 * @author vadim
 */
public class ChestStorage {

	private final SessionProvider provider;
	private final ChestRegistry registry;

	public ChestStorage(SessionProvider provider, ChestRegistry registry) {
		this.provider = provider;
		this.registry = registry;
	}

	@SneakyThrows
	public void load() {
		try (DatabaseSession session = provider.session()) {
			Dao<LinkedEnderChest, UUID> dao = session.getDAO(LinkedEnderChest.class, UUID.class);

			Iterator<LinkedEnderChest> iterator = dao.queryForAll().iterator();
			LinkedEnderChest gen;
			while (iterator.hasNext()) {
				gen = iterator.next();

				// gen world is lazily set from uuid field upon getter invokation
//				registry.createLinkedEnderChest(gen);
			}
		}
	}

	@SneakyThrows
	public void save() {
		try (DatabaseSession session = provider.session()) {
			Dao<LinkedEnderChest, UUID> dao = session.getDAO(LinkedEnderChest.class, UUID.class);
			TableUtils.clearTable(session.getConnectionSource(), dao.getDataClass());

//			Iterator<LinkedEnderChest> iterator = registry.();
//			LinkedEnderChest gen;
//			while (iterator.hasNext()) {
//				gen = (LinkedEnderChest) iterator.next();
//				dao.create(gen);
//			}
		}
	}

}
