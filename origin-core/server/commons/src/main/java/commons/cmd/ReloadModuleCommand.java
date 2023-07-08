package commons.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import commons.Commons;
import commons.util.StringUtil;
import me.vadim.util.conf.ConfigurationManager;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

import static commons.cmd.Prefix.MODULES;

/**
 * @author vadim
 */
@CommandAlias("reload-module")
public final class ReloadModuleCommand extends BaseCommand {

	public final Map<String, ConfigurationManager> managers = new HashMap<>();

	@Default("reload")
	@CommandPermission("*")
	@CommandCompletion("@modules")
	public void reload(CommandSender sender, String module) {
		if (!sender.isOp()) return;
		Commons.scheduler().getAsyncExecutor().submit(() -> {
			StringUtil.send(sender, MODULES + "&eReloading &b" + module + "&e...");
			managers.get(module).reload();
			StringUtil.send(sender, MODULES + "&aDone!");
		});
	}

	@CommandAlias("reload-modules")
	@CommandPermission("*")
	public void reload(CommandSender sender) {
		if (!sender.isOp()) return;
		Commons.scheduler().getAsyncExecutor().submit(() -> {
			for (Map.Entry<String, ConfigurationManager> entry : managers.entrySet()) {
				StringUtil.send(sender, MODULES + "&eReloading &b" + entry.getKey() + "&e...");
				entry.getValue().reload();
			}
			StringUtil.send(sender, MODULES + "&aDone!");
		});
	}

}
