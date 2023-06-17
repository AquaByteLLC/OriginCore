package generators.impl;

import co.aikar.commands.PaperCommandManager;
import generators.impl.data.GenAccount;
import generators.impl.cmd.GenCommand;
import generators.impl.conf.Config;
import generators.impl.conf.Messages;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author vadim
 */
public class GensPlugin extends JavaPlugin implements ResourceProvider {

	private LiteConfig lfc;
	private PaperCommandManager  commands;
	private GenRegistry registry;
	private GenHandler handler;

	public Config config() {
		return lfc.open(Config.class);
	}

	public Messages messages() {
		return lfc.open(Messages.class);
	}

	@Override
	public void onEnable() {
		lfc = new LiteConfig(this);
		lfc.register(Config.class, Config::new);
		lfc.register(Messages.class, Messages::new);
		lfc.reload();

		commands = new PaperCommandManager(this);
		commands.registerCommand(new GenCommand());

		registry = new GenRegistry(lfc);
		handler = new GenHandler(lfc, registry);
	}

	@Override
	public void onDisable() {
		handler.shutdown();
		registry.flushAndSave();
	}

}
