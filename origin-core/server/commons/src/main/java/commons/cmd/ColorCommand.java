package commons.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import commons.interpolation.impl.InterpolationType;
import commons.util.StringUtil;
import org.bukkit.command.CommandSender;

import java.awt.*;

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

	@Default
	public void test(CommandSender sender) {
		sender.sendMessage(StringUtil.colorize(
				StringUtil.interpolateColor("Testing With This Message", new Color(0, 255, 224), new Color(0, 86, 255), InterpolationType.elastic)
		));
	}


}
