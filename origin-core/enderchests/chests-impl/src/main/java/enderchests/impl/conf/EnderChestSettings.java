package enderchests.impl.conf;

import commons.Commons;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import settings.EnumeratedSetting;
import settings.Settings;
import settings.setting.Setting;
import settings.setting.SettingOption;
import settings.setting.SettingSection;
import settings.setting.builder.SectionBuilder;
import settings.setting.builder.SettingsFactory;
import settings.setting.key.LocalKey;

/**
 * @author vadim
 */
public enum EnderChestSettings implements EnumeratedSetting {

	PARTICLES(Settings.api().getFactory().newSettingBuilder()
					  .setName("Particles")
					  .setDescription("Particle Settings")
					  .addOptions(Settings.api().getFactory().boolOptON("EnderChest particles will show.", "EnderChest Particles won't show."))
					  .setDefaultOption(0)
					  .setMenuItem(ItemStackBuilder.of(Material.NETHER_STAR).build())
					  .build()),
	SOUNDS(Settings.api().getFactory().newSettingBuilder()
				   .setName("Sounds")
				   .setDescription("Sound Settings")
				   .addOptions(Settings.api().getFactory().boolOptON("EnderChest sounds will play.", "EnderChest sounds won't play."))
				   .setDefaultOption(0)
				   .setMenuItem(ItemStackBuilder.of(Material.NOTE_BLOCK).build())
				   .build())
	;

	public static final SettingSection section;

	@Override
	public @NotNull Setting getSetting() {
		return setting;
	}

	@Override
	public boolean isEnabled(Player player) {
		if(setting.getOptions().size() != 2)
			EnumeratedSetting.nonBinarySetting();
		return getOption(player).getName().equalsIgnoreCase(SettingsFactory.BOOL_ON);
	}

	@Override
	public boolean isSelected(LocalKey option, Player player) {
		return getOption(player).getKey().equals(option);
	}

	@Override
	public @NotNull SettingOption getOption(Player player) {
		return Settings.api().getSettings(player).getOption(setting);
	}

	static {
		SectionBuilder builder = Settings.api().getFactory().newSectionBuilder();

		for (EnderChestSettings value : values())
			builder.addSetting(value.setting);

		System.out.println(Commons.config().getEChestsSettingsIcon().displayName().compact().toString() + " : ECHESTNAME");
		builder.setMenuItem(Commons.config().getEChestsSettingsIcon());

		section = builder.build();
	}

	private final Setting setting;

	EnderChestSettings(Setting setting) {
		this.setting = setting;
	}

	public static void init(JavaPlugin plugin) {
		Settings.api().getSections().createSection(plugin, section);
	}

}
