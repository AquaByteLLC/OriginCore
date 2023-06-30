package enderchests.impl.data;

import com.j256.ormlite.dao.Dao;
import commons.data.ORMLiteAccountStorage;
import commons.data.SessionProvider;
import enderchests.ChestRegistry;
import generators.GeneratorRegistry;
import me.vadim.util.conf.ConfigurationProvider;

import java.sql.SQLException;
import java.util.UUID;

/**
 * @author vadim
 */
public class EnderChestStorage extends ORMLiteAccountStorage<EnderChestAccount> {

	private final ChestRegistry registry;

	public EnderChestStorage(ChstRegistry registry, ConfigurationProvider conf, SessionProvider provider) {
		super(uuid -> new EnderChestAccount(uuid, registry, conf), provider, EnderChestAccount.class);
		this.registry = registry;
	}

	@Override
	protected void save(EnderChestAccount account, Dao<EnderChestAccount, UUID> dao) throws SQLException {
		dao.createOrUpdate(account);
	}

	@Override
	protected EnderChestAccount load(UUID uuid, Dao<EnderChestAccount, UUID> dao) throws SQLException {
		EnderChestAccount account = dao.queryForId(uuid);
		if(account == null)
			account = factory.create(uuid);
		account.registry = registry;
		return account;
	}

}
