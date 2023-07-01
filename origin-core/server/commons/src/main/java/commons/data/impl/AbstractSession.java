package commons.data.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import commons.data.DatabaseSession;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vadim
 */
public abstract class AbstractSession implements DatabaseSession {

	private final ConnectionSource   source;

	public AbstractSession(ConnectionSource source) {
		this.source = source;
	}

	@Override
	public final ConnectionSource getConnectionSource() {
		return source;
	}

	private final List<Dao<?, ?>> daos = new ArrayList<>();

	private final void table(Dao<?, ?> dao) throws SQLException {
		TableUtils.createTableIfNotExists(source, dao.getDataClass());
	}

	@Override
	@SneakyThrows
	public <T, I> Dao<T, I> getDAO(Class<T> objClass, Class<I> idClass) {
		Dao<T, I> dao = DaoManager.createDao(source, objClass);
		table(dao);
		daos.add(dao);
		return dao;
	}

	@Override
	@SneakyThrows
	public final void close() {
		for (Dao<?, ?> dao : daos)
			try(DatabaseConnection connection = source.getReadWriteConnection(dao.getTableName())) {
				table(dao);
				connection.setAutoCommit(false);
				dao.commit(connection);
			}

		source.close();
	}

}
