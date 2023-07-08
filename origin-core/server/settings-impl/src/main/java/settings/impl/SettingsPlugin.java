package settings.impl;

import co.aikar.commands.PaperCommandManager;
import commons.Commons;
import commons.OriginModule;
import commons.data.account.AccountStorage;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.ConfigurationManager;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import settings.Settings;
import settings.impl.cmd.SettingsCommand;
import settings.impl.conf.Config;
import settings.impl.data.SettingsAccount;
import settings.impl.data.SettingsAccountStorage;
import settings.impl.registry.GlobalSettingsRegistry;
import settings.impl.registry.PluginSectionRegistry;
import settings.impl.setting.builder.SettingsFactoryImpl;
import settings.impl.setting.key.GKey;
import settings.impl.setting.key.LKey;
import settings.registry.SectionRegistry;
import settings.registry.SettingsRegistry;
import settings.setting.builder.SettingsFactory;

public class SettingsPlugin extends JavaPlugin implements ResourceProvider, OriginModule {

	private static SettingsPlugin zlurpyIsCringe;

	public static SettingsPlugin singletonCringe() {
		return zlurpyIsCringe;
	}

	private AccountStorage<SettingsAccount> accounts;

	@Override
	public AccountStorage<SettingsAccount> getAccounts() {
		return accounts;
	}

	private LiteConfig lfc;

	@Override
	public ConfigurationManager getConfigurationManager() {
		return lfc;
	}

	public Config config() {
		return lfc.open(Config.class);
	}

	@Override
	public void onLoad() {
		zlurpyIsCringe = this;
	}

	private PaperCommandManager commands;

	@Override
	public void onEnable() {
		lfc = new LiteConfig(this);
		lfc.register(Config.class, Config::new);
		lfc.reload();

		Commons.commons().registerModule(this);

		SectionRegistry sectionRegistry = new PluginSectionRegistry();
		SettingsRegistry settingsRegistry = new GlobalSettingsRegistry();
		SettingsFactory settingsFactory = new SettingsFactoryImpl();
		new Settings(sectionRegistry, settingsRegistry, settingsFactory, (player) -> accounts.getAccount(player).getSettings(), LKey::convert, GKey::convert);

		accounts = new SettingsAccountStorage(Commons.db(), sectionRegistry);

		commands = new PaperCommandManager(this);
		commands.registerCommand(new SettingsCommand(accounts));

		// temp

		SettingsFactory factory = Settings.api().getFactory();

		// example "Mining" section
		Settings.api().getSections().createSection(this,
												   factory.newSectionBuilder()
														  .setName("Mining")
														  .setMenuItem(ItemStackBuilder.of(Material.IRON_PICKAXE).build())
														  .setDescription("Mining Settings")
														  .addSetting(factory.newSettingBuilder()
																			 .setName("Particles")
																			 .setDescription("Particle Settings")
																			 .addOptions(factory.newOptionBuilder()
																								.setName("ON")
																								.setDescription("Particles will show!")
																								.build())
																			 .addOptions(factory.newOptionBuilder()
																								.setName("MINIMAL")
																								.setDescription("Particles are reduced!")
																								.build())
																			 .addOptions(factory.newOptionBuilder()
																								.setName("OFF")
																								.setDescription("Particles won't show!")
																								.build())
																			 .setDefaultOption(0)
																			 .setMenuItem(ItemStackBuilder.of(Material.NETHER_STAR).build())
																			 .build())
														  .build());
	}

	@Override
	public void onDisable() {

	}

}
