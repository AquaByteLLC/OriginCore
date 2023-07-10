package enderchests.impl.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import commons.data.account.AccountProvider;
import commons.util.StringUtil;
import enderchests.ChestRegistry;
import enderchests.NetworkColor;
import enderchests.impl.conf.Config;
import enderchests.impl.data.EnderChestAccount;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author vadim
 */
@CommandAlias("echest|enderchests|enderchest")
public class EnderChestCommand extends BaseCommand {

	private final ChestRegistry registry;
	private final AccountProvider<EnderChestAccount> accounts;
	private final ConfigurationProvider conf;

	public EnderChestCommand(ChestRegistry registry, AccountProvider<EnderChestAccount> accounts, ConfigurationProvider conf) {
		this.registry = registry;
		this.accounts = accounts;
		this.conf     = conf;
	}

	@Subcommand("activecolor")
	void activeColor(Player sender, NetworkColor color) {
		accounts.getAccount(sender).temp = color;
		sender.sendMessage(color.chatColor + color.name());
	}

	@Subcommand("give")
	@CommandPermission("*")
	void give(CommandSender sender, @Flags("other") Player target) {
		StringUtil.send(sender, "&eGiving &b" + target.getName() + "&e an EnderChest.");

		ItemStack echest = conf.open(Config.class).getEnderChestItem();
		for (ItemStack item : target.getInventory().addItem(echest).values())
			target.getWorld().dropItem(target.getLocation(), item);
	}

	@Subcommand("setmaxslots")
	@CommandPermission("*")
	public void setMaxSlots(CommandSender sender, @Flags("other") Player target, int maxSlots) {
		accounts.getAccount(target).slotLimit = maxSlots;
	}

	@Subcommand("getmaxslots")
	@CommandPermission("*")
	public void getMaxSlots(CommandSender sender, @Flags("other") Player target) {
		StringUtil.send(sender, "&enum slots: &b" + accounts.getAccount(target).slotLimit);
	}

}
