package settings.impl.account;

import com.j256.ormlite.dao.Dao;
import commons.data.SessionProvider;
import commons.data.impl.ORMLiteAccountStorage;
import settings.registry.SettingsHolder;

import java.sql.SQLException;
import java.util.UUID;

public class SettingsAccountStorage extends ORMLiteAccountStorage<SettingsAccount> {

	public SettingsAccountStorage(SessionProvider provider) {
		super(SettingsAccount::new, provider, SettingsAccount.class);
	}

	@Override
	protected void save(SettingsAccount account, Dao<SettingsAccount, UUID> dao) throws SQLException {
		dao.createOrUpdate(account);
	}

	@Override
	protected SettingsAccount load(UUID uuid, Dao<SettingsAccount, UUID> dao) throws SQLException {
		SettingsAccount account = dao.queryForId(uuid);
		if (account == null)
			account = factory.create(uuid);
		account.init();
		return account;
	}

}
