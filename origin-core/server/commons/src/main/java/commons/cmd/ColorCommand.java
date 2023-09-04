package commons.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commons.interpolation.impl.InterpolationType;
import commons.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * @author vadim
 */
@CommandAlias("color")
@CommandPermission("*")
public class ColorCommand extends BaseCommand {

	private static final InterpolationType[] workForChat = {
			InterpolationType.linear,
			InterpolationType.smoothStep,
			InterpolationType.smoother,
			InterpolationType.slowFast,
			InterpolationType.sine,
			InterpolationType.sineIn,
			InterpolationType.sineOut,
			InterpolationType.cosine,
			InterpolationType.cosineIn,
			InterpolationType.cosineOut,
			InterpolationType.circle,
			InterpolationType.circleIn,
			InterpolationType.circleOut,
			InterpolationType.bounce,
			InterpolationType.bounceIn,
			InterpolationType.bounceOut
	};

	@Subcommand("test")
	public void test(CommandSender sender, String value) {
		sender.sendMessage(StringUtil.colorize(value));
	}

	@Subcommand("list")
	public void list(CommandSender sender, @Optional String message) {
		if (message == null)
			message = "The quick brown fox jumps over the lazy dog.";

		for (InterpolationType value : InterpolationType.values())
			sender.sendMessage(StringUtils.rightPad(value.name(), Arrays.stream(workForChat).mapToInt(it -> it.name().length()).max().getAsInt()) + StringUtil.colorize(String.format("&#<ff0000:0000ff;%s>%s&r", value.name(), message)));

//		sender.sendMessage(StringUtil.colorize(
//				StringUtil.interpolateColor("Testing With ",
//						new Color[]{
//								new Color(255, 0, 0),
//								new Color(192, 102, 0),
//								new Color(54, 255, 0),
//								new Color(85, 53, 255)
//						},
//						new double[]{
//								0.1,
//								0.8,
//								0.1
//						}, InterpolationType.smoothStep2)
//		));
	}


}
