package generators.impl.data;

import com.j256.ormlite.dao.Dao;
import commons.data.impl.ORMLiteAccountStorage;
import commons.data.SessionProvider;
import generators.GeneratorRegistry;
import me.vadim.util.conf.ConfigurationProvider;

import java.sql.SQLException;
import java.util.UUID;

/**
 * @author vadim
 */
public class GenAccountStorage extends ORMLiteAccountStorage<GenAccount> {

	private final GeneratorRegistry registry;

	public GenAccountStorage(GeneratorRegistry registry, ConfigurationProvider conf, SessionProvider provider) {
		super(uuid -> new GenAccount(uuid, registry, conf), provider, GenAccount.class);
		this.registry = registry;
	}

	@Override
	protected void save(GenAccount account, Dao<GenAccount, UUID> dao) throws SQLException {
		dao.createOrUpdate(account);
	}

	@Override
	protected GenAccount load(UUID uuid, Dao<GenAccount, UUID> dao) throws SQLException {
		GenAccount account = dao.queryForId(uuid);
		if (account == null)
			account = factory.create(uuid);
		account.registry = registry;
		return account;
	}

}
