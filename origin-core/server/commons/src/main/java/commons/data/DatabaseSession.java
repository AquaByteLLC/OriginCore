package commons.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.util.UUID;

/**
 * @author vadim
 */
public interface DatabaseSession extends AutoCloseable {

	ConnectionSource getConnectionSource();

	<T> Dao<T, UUID> getDAO(Class<T> clazz);

	@Override
	void close();

}