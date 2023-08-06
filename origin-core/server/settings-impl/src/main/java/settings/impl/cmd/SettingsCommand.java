package settings.impl.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import commons.data.account.AccountProvider;
import org.bukkit.entity.Player;
import settings.impl.data.SettingsAccount;

@CommandAlias("settings|options|opts")
public class SettingsCommand extends BaseCommand {

	private final AccountProvider<SettingsAccount> accounts;

	public SettingsCommand(AccountProvider<SettingsAccount> accounts) {
		this.accounts = accounts;
	}

	@Default
	void global(Player sender) {
		accounts.getAccount(sender).openMenu();
	}

}
