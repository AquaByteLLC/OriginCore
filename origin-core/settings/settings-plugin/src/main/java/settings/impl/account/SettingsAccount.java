package settings.impl.account;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import commons.data.AbstractAccount;
import settings.impl.registry.PlayerSettingsRegistry;

import java.util.UUID;

@DatabaseTable(tableName = "settingsAccount")
public class SettingsAccount extends AbstractAccount {

	@DatabaseField(dataType = DataType.SERIALIZABLE)
	PlayerSettingsRegistry registry;

	private SettingsAccount() { // ORMLite
		super(null);
		this.registry = new PlayerSettingsRegistry();
	}

	SettingsAccount(UUID uuid) {
		super(uuid);
		this.registry = new PlayerSettingsRegistry();
	}

	public PlayerSettingsRegistry getRegistry() {
		return registry;
	}
}
