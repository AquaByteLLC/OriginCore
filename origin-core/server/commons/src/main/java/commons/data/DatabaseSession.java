package commons.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.util.UUID;

/**
 * @author vadim
 */
public interface DatabaseSession extends AutoCloseable {

	ConnectionSource getConnectionSource();

	<T, I> Dao<T, I> getDAO(Class<T> objClass, Class<I> idClass);

	@Override
	void close();

}