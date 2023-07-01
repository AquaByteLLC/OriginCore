package enderchests.impl.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import commons.data.AccountProvider;
import enderchests.ChestRegistry;
import enderchests.NetworkColor;
import enderchests.impl.data.EnderChestAccount;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
@CommandAlias("echest|enderchests|enderchest")
public class EnderChestCommand extends BaseCommand {

	private final ChestRegistry registry;
	private final AccountProvider<EnderChestAccount> accounts;

	public EnderChestCommand(ChestRegistry registry, AccountProvider<EnderChestAccount> accounts) {
		this.registry = registry;
		this.accounts = accounts;
	}

	@Subcommand("activeColor")
	void activeColor(Player sender, NetworkColor color) {
		accounts.getAccount(sender).temp = color;
		sender.sendMessage(color.chatColor + color.name());
	}

}
