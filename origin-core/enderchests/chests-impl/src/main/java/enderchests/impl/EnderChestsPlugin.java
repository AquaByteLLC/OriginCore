package enderchests.impl;

import blocks.BlocksAPI;
import co.aikar.commands.PaperCommandManager;
import commons.Commons;
import commons.OriginModule;
import commons.data.account.AccountStorage;
import enderchests.ChestRegistry;
import enderchests.impl.cmd.EnderChestCommand;
import blocks.impl.cmd.IllusionCommand;
import enderchests.impl.conf.Config;
import enderchests.impl.data.EChestAccountStorage;
import enderchests.impl.data.EnderChestAccount;
import me.vadim.util.conf.ConfigurationManager;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author vadim
 */
public class EnderChestsPlugin extends JavaPlugin implements ResourceProvider, OriginModule {

	private static EnderChestsPlugin badDesignPatterns;

	public static EnderChestsPlugin singletonCringe() {
		return badDesignPatterns;
	}

	private final LiteConfig lfc = new LiteConfig(this);
	private PaperCommandManager commands;
	private ChestRegistry     chestRegistry;
	private EnderChestHandler chestHandler;
	private EChestAccountStorage accountStorage;

	public Config config() {
		return lfc.open(Config.class);
	}

	@Override
	public ConfigurationManager getConfigurationManager() {
		return lfc;
	}

	public ChestRegistry getChestRegistry() {
		return chestRegistry;
	}

	@Override
	public AccountStorage<EnderChestAccount> getAccounts() {
		return accountStorage;
	}

	@Override
	public void onLoad() {
		badDesignPatterns = this;
	}

	@Override
	public void onEnable() {
		lfc.register(Config.class, Config::new);
		lfc.reload();

		Commons.commons().registerModule(this);

		accountStorage = new EChestAccountStorage(lfc, Commons.db());
		chestRegistry = new EnderChestRegistry(lfc, accountStorage);
		chestHandler  = new EnderChestHandler(this, chestRegistry, accountStorage, Commons.events());

		commands = new PaperCommandManager(this);
		commands.registerCommand(new EnderChestCommand(chestRegistry, accountStorage));
	}

	@Override
	public void onDisable() {

	}

}
