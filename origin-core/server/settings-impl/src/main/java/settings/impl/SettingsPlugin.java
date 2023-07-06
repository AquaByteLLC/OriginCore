package settings.impl;

import co.aikar.commands.PaperCommandManager;
import commons.CommonsPlugin;
import commons.data.AccountProvider;
import commons.data.AccountStorage;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.Material;
import settings.Settings;
import settings.builder.SettingsFactory;
import settings.impl.account.SettingsAccount;
import settings.impl.account.SettingsAccountStorage;
import settings.impl.builder.SettingsFactoryImpl;
import settings.impl.cmd.SettingsCommand;
import settings.impl.conf.Config;
import settings.impl.registry.GlobalSettingsRegistry;
import settings.impl.registry.PluginSectionRegistry;

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
		accounts = new SettingsAccountStorage(CommonsPlugin.commons().getDatabase());

		CommonsPlugin.commons().registerAccountLoader(accounts);

		new Settings(new PluginSectionRegistry(), new GlobalSettingsRegistry(), new SettingsFactoryImpl()); // sadge

		commands = new PaperCommandManager(this);
		commands.registerCommand(new SettingsCommand(accounts));


		// temp

		SettingsFactory factory = Settings.api().getFactory();

		// example "Mining" section
		Settings.api().getSections().createSection(this,
												   factory.newSectionBuilder()
														  .setName("Mining")
														  .setMenuItem(ItemStackBuilder.of(Material.WOODEN_AXE).build())
														  .setDescription("Mining Settings")
														  .addSetting(factory.newSettingsBuilder()
																			 .setName("Particle")
																			 .setDescription("Particle Settings")
																			 .addOptions(factory.newOptionsBuilder()
																								.setName("ON")
																								.setDescription("Particles will show!")
																								.build())
																			 .addOptions(factory.newOptionsBuilder()
																								.setName("OFF")
																								.setDescription("Particles won't show!")
																								.build())
																			 .setDefaultOption(0)
																			 .setMenuItem(ItemStackBuilder.of(Material.STONE_AXE).build())
																			 .build())
														  .build());
	}

}
