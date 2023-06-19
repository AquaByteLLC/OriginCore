package commons.data.impl;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import me.vadim.util.conf.ResourceProvider;

import java.io.File;
import java.sql.SQLException;

/**
 * @author vadim
 */
public class SQLiteSession extends AbstractSession {

	public SQLiteSession(File folder) throws SQLException {
		super(new JdbcConnectionSource(String.format("jdbc:sqlite:%s", new File(folder, "test.db").getAbsolutePath())));
	}

}
