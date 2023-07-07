package commons.data.sql;

import java.sql.SQLException;

/**
 * @author vadim
 */
@FunctionalInterface
public interface SessionProvider {

	DatabaseSession session() throws SQLException;

}
