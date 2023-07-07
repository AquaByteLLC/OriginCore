package commons.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import commons.Commons;
import commons.CommonsPlugin;
import commons.data.AccountProvider;
import commons.data.AccountStorage;
import commons.econ.BankAccount;
import commons.econ.Transaction;
import commons.econ.TransactionResponse;
import commons.impl.OriginCurrency;
import commons.impl.account.AccountStorageHandler;
import commons.impl.account.PlayerDefaultAccount;
import commons.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

/**
 * @author vadim
 */
@CommandAlias("save-accounts")
public class SaveCommand extends BaseCommand {

	private final AccountStorageHandler handler;

	public SaveCommand(AccountStorageHandler handler) {
		this.handler = handler;
	}

	@Default
	@CommandPermission("*")
	void save(CommandSender sender) {
		if(!sender.isOp()) return;
		Commons.scheduler().getAsyncExecutor().submit(() -> {
			StringUtil.send(sender, "&ePlease wait...");
			int err = 0;
			for (AccountStorage<?> storage : handler.getStorages()) {
				StringUtil.send(sender, String.format("&b> &eSaving &b[&7%s&b]", storage.getAccountClass().getSimpleName()));
				try {
					storage.flushAndSave();
				} catch (Exception e) {
					err++;
					String msg = e.getMessage();
					if(msg == null || "null".equals(msg)) msg = "";
					StringUtil.send(sender, String.format("&4[&c!&4] &c&l%s&4(&d%s&4)", e.getClass().getSimpleName(), msg));
				}
			}
			if(err == 0)
				StringUtil.send(sender, "&2All accounts saved!");
			else
				StringUtil.send(sender, "&eCompleted with errors: some accounts not saved.");
		});
	}

}
