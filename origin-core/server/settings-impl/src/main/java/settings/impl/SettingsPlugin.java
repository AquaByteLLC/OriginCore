package settings.impl;

import co.aikar.commands.PaperCommandManager;
import commons.Commons;
import commons.CommonsPlugin;
import commons.data.account.AccountProvider;
import commons.data.account.AccountStorage;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.Material;
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

public class SettingsPlugin extends ExtendedJavaPlugin implements ResourceProvider {

	private static SettingsPlugin zlurpyIsCringe;

	public static SettingsPlugin singletonCringe() {
		return zlurpyIsCringe;
	}

	private LiteConfig lfc;
	private AccountStorage<SettingsAccount> accounts;

	public ConfigurationProvider getConfiguration() {
		return lfc;
	}

	public Config config() {
		return lfc.open(Config.class);
	}

	public AccountProvider<SettingsAccount> getAccounts() {
		return accounts;
	}

	@Override
	protected void load() {
		zlurpyIsCringe = this;
	}

	private PaperCommandManager commands;

	@Override
	protected void enable() {
		lfc = new LiteConfig(this);
		lfc.register(Config.class, Config::new);
		lfc.reload();

		Commons.commons().registerReloadHook(this, lfc);

		SectionRegistry sectionRegistry = new PluginSectionRegistry();
		SettingsRegistry settingsRegistry = new GlobalSettingsRegistry();
		SettingsFactory settingsFactory = new SettingsFactoryImpl();
		new Settings(sectionRegistry, settingsRegistry, settingsFactory, (player) -> accounts.getAccount(player).getSettings(), LKey::convert, GKey::convert);

		accounts = new SettingsAccountStorage(CommonsPlugin.commons().getDatabase(), sectionRegistry);
		CommonsPlugin.commons().registerAccountLoader(accounts);

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

}
