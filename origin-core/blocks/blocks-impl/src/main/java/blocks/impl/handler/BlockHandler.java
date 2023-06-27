package blocks.impl.handler;


import blocks.BlocksAPI;
import me.vadim.util.conf.LiteConfig;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.function.Consumer;

public interface BlockHandler {

	LiteConfig lfc = BlocksAPI.get().getInstance(LiteConfig.class);
	static void init(Consumer<YamlConfiguration> configurationConsumer) {
		final File blocksFile = lfc.open(BlocksConfig.class).file;
		final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(blocksFile);
		configurationConsumer.accept(configuration);
	}
}