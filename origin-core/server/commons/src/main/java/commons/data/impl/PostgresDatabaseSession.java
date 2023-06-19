package commons.data.impl;

import commons.data.DatabaseSession;

import java.sql.Connection;

/**
 * @author vadim
 */
public class PostgresDatabaseSession extends AbstractSession {

	//todo: config values for login and db spec

	public PostgresDatabaseSession() {
		super(null);
	}

}
