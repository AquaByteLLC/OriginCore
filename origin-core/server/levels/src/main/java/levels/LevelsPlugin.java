package levels;

import co.aikar.commands.PaperCommandManager;
import commons.Commons;
import commons.OriginModule;
import commons.data.account.AccountStorage;
import commons.data.sql.SessionProvider;
import commons.events.api.EventRegistry;
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

	@Override
	public AccountStorage<?> getAccounts() {
		return null;
	}

	@Override
	public ConfigurationManager getConfigurationManager() {
		return lfc;
	}

	@Override
	public void onEnable() {
		lfc.reload();

		Commons.commons().registerModule(this);

		SessionProvider db = Commons.db();
		EventRegistry events  = Commons.events();

		commands = new PaperCommandManager(this);
	}


}
