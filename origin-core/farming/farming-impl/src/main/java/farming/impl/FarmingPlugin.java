package farming.impl;

import blocks.BlocksAPI;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.builder.OriginBlock;
import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Commons;
import commons.OriginModule;
import commons.data.account.AccountStorage;
import commons.events.api.EventRegistry;
import farming.impl.action.FarmingActions;
import farming.impl.commands.FarmingCommands;
import farming.impl.conf.BlocksConfig;
import farming.impl.conf.GeneralConfig;
import farming.impl.events.FarmingEvents;
import farming.impl.hoe.enchants.EnchantTypes;
import farming.impl.hoe.enchants.abilities.Abilities;
import farming.impl.settings.FarmingSettings;
import lombok.Getter;
import me.vadim.util.conf.ConfigurationManager;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tools.impl.ToolsPlugin;

import java.io.IOException;

public class FarmingPlugin extends JavaPlugin implements ResourceProvider, OriginModule {

	@Getter private static Injector injector;
	private BlocksAPI blocksAPI;
	private BlocksPlugin blocksPlugin;
	public static LiteConfig lfc;

	public GeneralConfig getGeneralConfig() {
		return lfc.open(GeneralConfig.class);
	}

	@Override
	public ConfigurationManager getConfigurationManager() {
		return lfc;
	}

	@Override
	public AccountStorage<?> getAccounts() { return null; }

	@Override
	public void onEnable() {
		EventRegistry eventRegistry = Commons.events();

		lfc = new LiteConfig(this);
		lfc.register(GeneralConfig.class, GeneralConfig::new);
		lfc.register(BlocksConfig.class, BlocksConfig::new);
		lfc.reload();

		Commons.commons().registerModule(this);

		this.blocksAPI = BlocksAPI.getInstance();
		this.blocksPlugin = BlocksPlugin.get().getInstance(BlocksPlugin.class);

		injector = Guice.createInjector(new FarmingModule(this, lfc, blocksAPI));

		EnchantTypes.init(ToolsPlugin.getPlugin().getEnchantRegistry(), ToolsPlugin.getPlugin().getEnchantFactory());
		FarmingEvents.init(eventRegistry);
		FarmingActions.init();
		Abilities.init();
		FarmingSettings.init(this);

		setupCommands();
		setupBlocksYml();
	}

	@Override
	public void onDisable() {
		try {
			save();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void afterReload() throws Exception {
		OriginModule.super.afterReload();
		save();
		FarmingActions.init();
	}

	void save() throws IOException {
		lfc.open(BlocksConfig.class).save();
		FarmingActions.init();
		Bukkit.getOnlinePlayers().forEach(player ->
				RegenerationRegistry.cancelRegenerations(player,
						blocksPlugin.getAccounts()
								.getAccount(player)
								.getRegenerationRegistry()
								.getRegenerations()));

	}

	@Override
	public void onSave() {
		try {
			save();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void setupCommands() {
		PaperCommandManager commands = new PaperCommandManager(this);
		commands.registerCommand(new FarmingCommands(blocksAPI.getBlockRegistry(), blocksAPI.getRegionRegistry()));
	}

	private void setupBlocksYml() {
		final BlocksConfig blocksConfig = lfc.open(BlocksConfig.class);

		blocksConfig.getConfiguration().getConfigurationSection("Blocks").getKeys(false).forEach(blockKey -> {
			final String mainBlocksPath = "Blocks." + blockKey + ".";
			final OriginBlock originBlock = blocksConfig.createOriginBlock(mainBlocksPath, blockKey);
			blocksAPI.getBlockRegistry().createBlock(originBlock);
		});
	}

	protected static class FarmingModule extends AbstractModule {
		private final FarmingPlugin plugin;
		private final LiteConfig lfc;
		private final BlocksAPI blocksAPI;

		FarmingModule(FarmingPlugin plugin, LiteConfig lfc, BlocksAPI blocksAPI) {
			this.plugin = plugin;
			this.lfc = lfc;
			this.blocksAPI = blocksAPI;
		}

		protected void configure() {
			this.bind(FarmingPlugin.class).toInstance(plugin);
			this.bind(LiteConfig.class).toInstance(lfc);
			this.bind(BlocksAPI.class).toInstance(blocksAPI);
		}
	}
}
