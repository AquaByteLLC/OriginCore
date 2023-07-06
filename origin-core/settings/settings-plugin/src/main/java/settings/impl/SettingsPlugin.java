package settings.impl;

import api.Setting;
import api.option.SettingsOption;
import api.section.SettingSection;
import commons.CommonsPlugin;
import commons.data.AccountStorage;
import commons.events.api.EventRegistry;
import lombok.Getter;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.Material;
import settings.impl.account.SettingsAccount;
import settings.impl.account.SettingsAccountStorage;
import settings.impl.builder.OptionsBuilder;
import settings.impl.builder.SectionBuilder;
import settings.impl.builder.SettingsBuilder;
import settings.impl.registry.SectionRegistry;
import settings.impl.registry.SettingsRegistry;

import java.util.List;

public class SettingsPlugin extends ExtendedJavaPlugin implements ResourceProvider {
	private SettingsAccountStorage storage;
	@Getter private static SectionRegistry sectionRegistry;
	@Getter private static SettingsRegistry settingsRegistry;
	private LiteConfig lfc;
	@Override
	protected void enable() {
		final CommonsPlugin commonsPlugin = CommonsPlugin.commons();
		lfc = new LiteConfig(this);
		final EventRegistry events = commonsPlugin.getEventRegistry();
		storage = new SettingsAccountStorage(commonsPlugin.getDatabase());

		commonsPlugin.registerAccountLoader(storage);
		settingsRegistry = new SettingsRegistry();
		sectionRegistry = new SectionRegistry();

		SettingsOption onOption = new OptionsBuilder().setName("ON").setDescription("Particles will show!").build();
		SettingsOption offOption = new OptionsBuilder().setName("OFF").setDescription("Particles won't show!").build();

		Setting setting = new SettingsBuilder()
				.setName("Particle")
				.setDescription(List.of("Particle Setting"))
				.setDefaultOption(onOption)
				.setOptions(List.of(onOption, offOption))
				.setMenuItem(ItemStackBuilder.of(Material.STONE_AXE).build())
				.build();

		settingsRegistry.createSetting(setting);

		SettingSection settingSection = new SectionBuilder()
				.setName("MiningSection")
				.setMenuItem(ItemStackBuilder.of(Material.WOODEN_AXE).build())
				.setDescription(List.of("Mining Settings"))
				.setRegistry(settingsRegistry)
				.build();

		sectionRegistry.createSection(this, settingSection);
	}

	public AccountStorage<SettingsAccount> getAccounts() {
		return storage;
	}
}
