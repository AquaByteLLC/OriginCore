package generators.impl.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import commons.data.DatabaseSession;
import commons.data.SessionProvider;
import commons.impl.PlayerOwned;
import generators.GeneratorRegistry;
import generators.impl.GenRegistry;
import generators.impl.wrapper.Gen;
import generators.wrapper.Generator;
import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.UUID;

/**
 * @author vadim
 */
public class GenStorage {

	private final SessionProvider provider;
	private final GeneratorRegistry registry;

	public GenStorage(SessionProvider provider, GeneratorRegistry registry) {
		this.provider = provider;
		this.registry = registry;
	}

	@SneakyThrows
	public void load() {
		try (DatabaseSession session = provider.session()) {
			Dao<Gen, UUID> dao = session.getDAO(Gen.class, UUID.class);

			Iterator<Gen> iterator = dao.queryForAll().iterator();
			Gen gen;
			while (iterator.hasNext()) {
				gen = iterator.next();

				// gen world is lazily set from uuid field upon getter invokation
				registry.createGen(gen);
			}
		}
	}

	@SneakyThrows
	public void save() {
		try (DatabaseSession session = provider.session()) {
			Dao<Gen, UUID> dao = session.getDAO(Gen.class, UUID.class);
			TableUtils.clearTable(session.getConnectionSource(), dao.getDataClass());

			Iterator<Generator> iterator = registry.all();
			Gen gen;
			while (iterator.hasNext()) {
				gen = (Gen) iterator.next();
				dao.create(gen);
			}
		}
	}

}
