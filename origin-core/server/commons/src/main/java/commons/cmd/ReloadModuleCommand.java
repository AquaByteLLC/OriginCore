package commons.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import commons.Commons;
import commons.OriginModule;
import commons.util.StringUtil;
import me.vadim.util.conf.ConfigurationManager;
import org.bukkit.command.CommandSender;

import java.util.Map;

import static commons.cmd.Prefix.*;

/**
 * @author vadim
 */
public final class ReloadModuleCommand extends ModuleCommand {

	public ReloadModuleCommand(Map<String, OriginModule> modulesView) {
		super(modulesView);
	}

	/**
	 * @return {@code true} if there was an error
	 */
	private static boolean reloadModule(CommandSender sender, OriginModule module) {
		try {
			ConfigurationManager conf = module.getConfigurationManager();
			if(conf != null)
				conf.reload();
			module.afterReload();
			return false;
		} catch (Throwable t) {
			t.printStackTrace();
			StringUtil.send(sender, MODULES + exception(t));
			return true;
		}
	}

	@CommandAlias("reload-module")
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
			StringUtil.send(sender, MODULES + "&eReloading &b" + module + "&e...");
			if(!reloadModule(sender, om))
				StringUtil.send(sender, MODULES + "&aDone!");
		});
	}

	@CommandAlias("reload-modules")
	@CommandPermission("*")
	public void reload(CommandSender sender) {
		if (!sender.isOp()) return;
		Commons.scheduler().getAsyncExecutor().submit(() -> {
			StringUtil.send(sender, MODULES + "&ePlease wait...");
			boolean err = false;
			for (OriginModule module : modulesView.values()) {
				StringUtil.send(sender, MODULES + "&eReloading &b" + StringUtil.formatModuleName(module) + "&e...");
				err |= reloadModule(sender, module);
			}
			if(!err)
				StringUtil.send(sender, MODULES + "&2Done!");
			else
				StringUtil.send(sender, MODULES + "&eCompleted with errors: some modules not reloaded.");
		});
	}

}
