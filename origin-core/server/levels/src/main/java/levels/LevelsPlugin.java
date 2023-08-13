package levels;

import co.aikar.commands.PaperCommandManager;
import commons.Commons;
import commons.CommonsPlugin;
import commons.OriginModule;
import commons.conf.SettableConfig;
import commons.data.account.AccountStorage;
import commons.data.sql.SessionProvider;
import commons.events.api.EventRegistry;
import commons.versioning.VersionSender;
import levels.cmd.LevelsCommand;
import levels.conf.LevelsConfig;
import levels.conf.action.LevelEffects;
import levels.conf.action.LevelMessages;
import levels.data.LevelsAccount;
import levels.data.LevelsAccountStorage;
import levels.registry.LevelRegistry;
import levels.registry.impl.LevelRegistryImpl;
import me.vadim.util.conf.ConfigurationManager;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author vadim
 */
public class LevelsPlugin extends JavaPlugin implements ResourceProvider, OriginModule {

	private LiteConfig lfc;
	private PaperCommandManager commands;
	private LevelRegistry levelRegistry;

	private LevelsAccountStorage accounts;

	@Override
	public AccountStorage<LevelsAccount> getAccounts() {
		return accounts;
	}

	@Override
	public ConfigurationManager getConfigurationManager() {
		return lfc;
	}

	public LevelsConfig config() {
		return lfc.open(LevelsConfig.class);
	}

	public LevelRegistry getLevelRegistry() {
		return levelRegistry;
	}

	@Override
	public void onEnable() {
		lfc = new LiteConfig(this);
		lfc.register(LevelsConfig.class, LevelsConfig::new);
		lfc.reload();

		SessionProvider db = Commons.db();
		EventRegistry events  = Commons.events();

		levelRegistry = LevelRegistryImpl.load(config());
		accounts = new LevelsAccountStorage(Commons.db(), levelRegistry);

		commands = new PaperCommandManager(this);
		commands.registerCommand(new LevelsCommand(this));

		Commons.commons().registerModule(this);
	}


}
