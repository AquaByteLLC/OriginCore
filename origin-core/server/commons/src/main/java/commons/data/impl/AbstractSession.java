package commons.data.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import commons.data.DatabaseSession;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

	private final List<Dao<?, UUID>> daos = new ArrayList<>();

	@Override
	@SneakyThrows
	public <T> Dao<T, UUID> getDAO(Class<T> clazz) {
		TableUtils.createTableIfNotExists(source, clazz);
		Dao<T, UUID> dao = DaoManager.createDao(source, clazz);
		daos.add(dao);
		return dao;
	}

	@Override
	@SneakyThrows
	public final void close() {
		for (Dao<?, UUID> dao : daos)
			try(DatabaseConnection connection = source.getReadWriteConnection(dao.getTableName())) {
				connection.setAutoCommit(false);
				dao.commit(connection);
			}

		source.close();
	}

}
