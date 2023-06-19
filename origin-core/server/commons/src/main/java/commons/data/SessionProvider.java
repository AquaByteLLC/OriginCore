package commons.data;

import java.sql.SQLException;

/**
 * @author vadim
 */
public interface SessionProvider {

	DatabaseSession session() throws SQLException;

}
