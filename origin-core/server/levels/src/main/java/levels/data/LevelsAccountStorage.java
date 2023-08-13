package levels.data;

import com.j256.ormlite.dao.Dao;
import commons.data.account.impl.ORMLiteAccountStorage;
import commons.data.sql.SessionProvider;
import levels.registry.LevelRegistry;

import java.sql.SQLException;
import java.util.UUID;

public class LevelsAccountStorage extends ORMLiteAccountStorage<LevelsAccount> {

	private final LevelRegistry registry;

	public LevelsAccountStorage(SessionProvider provider, LevelRegistry registry) {
		super(LevelsAccount::new, provider, LevelsAccount.class);
		this.registry = registry;
	}

	@Override
	protected void save(LevelsAccount account, Dao<LevelsAccount, UUID> dao) throws SQLException {
		dao.createOrUpdate(account);
	}

	@Override
	protected LevelsAccount load(UUID uuid, Dao<LevelsAccount, UUID> dao) throws SQLException {
		LevelsAccount account = dao.queryForId(uuid);
		if (account == null)
			account = factory.create(uuid);
		account.registry = registry;
		return account;
	}

}
