package commons.cmd;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import commons.Commons;
import commons.OriginModule;
import commons.data.account.AccountStorage;
import commons.util.StringUtil;
import org.bukkit.command.CommandSender;

import java.util.Map;

import static commons.cmd.Prefix.*;

/**
 * @author vadim
 */
public class SaveModuleCommand extends ModuleCommand {

	public SaveModuleCommand(Map<String, OriginModule> modulesView) {
		super(modulesView);
	}

	/**
	 * @return {@code true} if there was an error
	 */
	private static boolean saveModule(CommandSender sender, OriginModule module) {
		try {
			AccountStorage<?> accounts = module.getAccounts();
			if(accounts != null)
				accounts.flushAndSave();
			module.onSave();
			return false;
		} catch (Throwable t) {
			t.printStackTrace();
			StringUtil.send(sender, MODULES + exception(t));
			return true;
		}
	}

	@CommandAlias("save-module")
	@CommandPermission("*")
	@CommandCompletion("@modules")
	public void reload(CommandSender sender, String module) {
		if (!sender.isOp()) return;
		OriginModule om = modulesView.get(module);
		if(om == null) {
			StringUtil.send(sender, ERR + "&cUnknown module '&b" + module + "&c'.");
			return;
		}

		Commons.scheduler().getAsyncExecutor().submit(() -> {
			StringUtil.send(sender, MODULES + String.format("&eSaving &b%s&e...", StringUtil.formatModuleName(om)));
			if(!saveModule(sender, om))
				StringUtil.send(sender, MODULES + "&aDone!");
		});
	}

	@CommandAlias("save-modules")
	@CommandPermission("*")
	public void reload(CommandSender sender) {
		if(!sender.isOp()) return;
		Commons.scheduler().getAsyncExecutor().submit(() -> {
			StringUtil.send(sender, MODULES + "&ePlease wait...");
			boolean err = false;
			for (OriginModule module : modulesView.values()) {
				StringUtil.send(sender, MODULES + String.format("&eSaving &b%s&e...", StringUtil.formatModuleName(module)));
				err |= saveModule(sender, module);
			}
			if(!err)
				StringUtil.send(sender, MODULES + "&2Done!");
			else
				StringUtil.send(sender, MODULES + "&eCompleted with errors: some accounts not saved.");
		});
	}

}
