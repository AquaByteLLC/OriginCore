package generators.impl.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import commons.StringUtil;
import commons.data.AccountProvider;
import commons.data.AccountStorage;
import generators.impl.data.GenAccount;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
@CommandAlias("gen")
public class GenCommand extends BaseCommand {

	private final AccountProvider<GenAccount> provider;

	public GenCommand(AccountProvider<GenAccount> provider) {
		this.provider = provider;
	}

	@Subcommand("setmaxslots")
	public void setMaxSlots(Player sender, int maxSlots) {
		provider.getAccount(sender).slotLimit = maxSlots;
	}

	@Subcommand("getmaxslots")
	public void getMaxSlots(Player sender) {
		StringUtil.send(sender, "&enum slots: &b" + provider.getAccount(sender).slotLimit);
	}

	@Subcommand("flushandsave")
	public void flushAndSave(Player sender) {
		((AccountStorage<GenAccount>) provider).flushAndSave();
		StringUtil.send(sender, "done");
	}

}
