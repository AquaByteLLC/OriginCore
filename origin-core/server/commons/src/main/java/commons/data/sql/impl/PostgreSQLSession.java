package commons.data.sql.impl;

import me.vadim.util.conf.ConfigurationProvider;

/**
 * @author vadim
 */
public class PostgreSQLSession extends AbstractSession {

	//todo: config values for login and db spec

	public PostgreSQLSession(ConfigurationProvider provider) {
		super(null);
	}

}
