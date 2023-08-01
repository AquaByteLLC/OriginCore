package commons.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import commons.util.StringUtil;
import org.bukkit.command.CommandSender;

/**
 * @author vadim
 */
@CommandAlias("color")
@CommandPermission("*")
public class ColorCommand extends BaseCommand {

	@Default
	public void color(CommandSender sender, String value) {
		sender.sendMessage(StringUtil.colorize(value));
	}


}
