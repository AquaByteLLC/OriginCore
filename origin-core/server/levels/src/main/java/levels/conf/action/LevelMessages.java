package levels.conf.action;

import commons.CommonsPlugin;
import commons.conf.SettableConfig;
import commons.versioning.VersionSender;
import commons.versioning.impl.ConfigurableMessage;
import lombok.SneakyThrows;

import java.util.List;


public class LevelMessages {
	public static VersionSender versionSender;

	public static ConfigurableMessage LEVEL_UP_MESSAGE;

	@SneakyThrows
	public static void init() {
		final SettableConfig cfg = new SettableConfig("level_messages.yml", "levels", CommonsPlugin.commons());
		versionSender = new VersionSender(cfg.getFile(), cfg.getFileConfiguration());

		LEVEL_UP_MESSAGE = new ConfigurableMessage(versionSender, "level_up",
				List.of("&7&m                                                       ",
						"",
						"&a&lOrigin&2&lMC &7| &a&l&#55ff55&lL&#47f147&lE&#39e339&lV&#2bd52b&lE&#1cc61c&lL&#0eb80e&lU&#00aa00&lP &7(Hooray)",
						"&7&oCongrats, you have &f&oleveled&7&o up!",
						"",
						" &2| &aInformation",
						" &2| &7Level: &f{level} &8- {prefix}",
						"",
						" &2| &aReward(s)",
						" &2| &7{rewards}"
				),
				List.of("&7&m                                                       ",
						"",
						"&a&lOrigin&2&lMC &7| &a&l&#55ff55&lL&#47f147&lE&#39e339&lV&#2bd52b&lE&#1cc61c&lL&#0eb80e&lU&#00aa00&lP &7(Hooray)",
						"&7&oCongrats, you have &f&oleveled&7&o up!",
						"",
						" &2| &aInformation",
						" &2| &7Level: &f{level} &8- {prefix}",
						"",
						" &2| &aReward(s)",
						" &2| &7{rewards}"
				)
		);

	}
}
