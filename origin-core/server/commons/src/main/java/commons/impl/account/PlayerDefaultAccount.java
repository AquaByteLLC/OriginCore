package commons.impl.account;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import commons.data.AbstractAccount;

import java.util.UUID;


@DatabaseTable
public class PlayerDefaultAccount extends AbstractAccount {

	private PlayerDefaultAccount() { // ORMLite
		super(null);
	}

	public PlayerDefaultAccount(UUID uuid) {
		super(uuid);
	}

	@DatabaseField
	public double tokenCount;

}
