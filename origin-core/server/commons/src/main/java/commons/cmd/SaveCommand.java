package commons.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import commons.Commons;
import commons.data.account.AccountStorage;
import commons.impl.data.account.AccountStorageHandler;
import commons.util.StringUtil;
import org.bukkit.command.CommandSender;

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
