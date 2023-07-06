package settings.impl.account;

import api.registry.PlayerSettingsRegistry;
import com.j256.ormlite.dao.Dao;
import commons.data.ORMLiteAccountStorage;
import commons.data.SessionProvider;

import java.sql.SQLException;
import java.util.UUID;

public class SettingsAccountStorage extends ORMLiteAccountStorage<SettingsAccount> {

	PlayerSettingsRegistry registry;

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
		this.registry = account.registry;
		return account;
	}
}
