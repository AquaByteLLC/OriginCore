package enderchests.impl.data;

import com.j256.ormlite.dao.Dao;
import commons.data.account.impl.ORMLiteAccountStorage;
import commons.data.sql.SessionProvider;
import me.vadim.util.conf.ConfigurationProvider;

import java.sql.SQLException;
import java.util.UUID;

/**
 * @author vadim
 */
public class EChestAccountStorage extends ORMLiteAccountStorage<EnderChestAccount> {

	public EChestAccountStorage(ConfigurationProvider conf, SessionProvider provider) {
		super(EnderChestAccount::new, provider, EnderChestAccount.class);
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
		return account;
	}

}
