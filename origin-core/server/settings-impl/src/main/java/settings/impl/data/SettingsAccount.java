package settings.impl.data;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import commons.data.impl.AbstractAccount;
import me.vadim.util.menu.Menu;
import org.bukkit.entity.Player;
import settings.Settings;
import settings.impl.SettingsPlugin;
import settings.impl.menu.GlobalSettingsMenu;
import settings.impl.registry.PlayerSettingsHolder;
import settings.registry.SettingsHolder;

import java.util.UUID;

@DatabaseTable
public class SettingsAccount extends AbstractAccount {

	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	byte[] serialized; // ORMLite

	PlayerSettingsHolder holder;
	GlobalSettingsMenu menu;

	private SettingsAccount() { // ORMLite
		super(null);
	}

	SettingsAccount(UUID uuid) {
		super(uuid);
		init();
	}

	void init() {
		this.holder = new PlayerSettingsHolder(getOwnerUUID());
		this.menu   = new GlobalSettingsMenu(SettingsPlugin.singletonCringe(), this.holder);
		Settings.api().getSections().flash(holder);
	}

	public SettingsHolder getSettings() {
		return holder;
	}

	public void openMenu() {
		Player player = getOfflineOwner().getPlayer();
		if (player == null)
			return; // silently
		Menu menu = this.menu.getMenu();
		menu.regen();
		menu.open(player);
	}

}
