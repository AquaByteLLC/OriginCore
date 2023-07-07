package commons.impl.data.account;

import com.j256.ormlite.dao.Dao;
import commons.data.account.impl.ORMLiteAccountStorage;
import commons.data.sql.SessionProvider;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerDefaultAccountStorage extends ORMLiteAccountStorage<PlayerDefaultAccount> {

	public PlayerDefaultAccountStorage(SessionProvider provider) {
		super(PlayerDefaultAccount::new, provider, PlayerDefaultAccount.class);
	}

	@Override
	protected void save(PlayerDefaultAccount account, Dao<PlayerDefaultAccount, UUID> dao) throws SQLException {
		dao.createOrUpdate(account);
	}

	@Override
	protected PlayerDefaultAccount load(UUID uuid, Dao<PlayerDefaultAccount, UUID> dao) throws SQLException {
		PlayerDefaultAccount account = dao.queryForId(uuid);
		if (account == null)
			account = factory.create(uuid);
		return account;
	}
}
