package blocks.impl.data.account;

import com.j256.ormlite.dao.Dao;
import commons.data.account.impl.ORMLiteAccountStorage;
import commons.data.sql.SessionProvider;

import java.sql.SQLException;
import java.util.UUID;

public class BlockAccountStorage extends ORMLiteAccountStorage<BlockAccount> {


	public BlockAccountStorage(SessionProvider provider) {
		super(uuid -> new BlockAccount(uuid), provider, BlockAccount.class);
	}

	@Override
	protected void save(BlockAccount account, Dao<BlockAccount, UUID> dao) throws SQLException {
		dao.createOrUpdate(account);
	}

	@Override
	protected BlockAccount load(UUID uuid, Dao<BlockAccount, UUID> dao) throws SQLException {
		BlockAccount account = dao.queryForId(uuid);
		if (account == null)
			account = factory.create(uuid);
		return account;
	}
}
