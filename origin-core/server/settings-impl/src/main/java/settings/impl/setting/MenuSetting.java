package settings.impl.setting;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import settings.Setting;
import settings.option.SettingsOption;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantValue")
public class MenuSetting extends Keyed<GlobalKey> implements Setting {

	private final String settingName;
	private final ItemStack menuItem;
	private final List<SettingOption> options;
	private final List<String> description;

	public MenuSetting(GlobalKey key, String settingName, ItemStack menuItem, List<String> description) {
		super(key);
		this.settingName = settingName;
		this.menuItem    = ItemBuilder.copy(menuItem).displayName(settingName).lore(description).build();
		this.description = description;
		this.options     = new ArrayList<>();
	}

	@Override
	public String getName() {
		return this.settingName;
	}

	@Override
	public ItemStack getMenuItem(SettingOption selectedOption) {
		Config config = SettingsPlugin.singletonCringe().config();

		List<String> footer = new ArrayList<>();
		for (SettingOption option : options)
			if (option.equals(selectedOption))
				footer.add(config.getSelectedOptionPrefix() + option.getDescription());
			else
				footer.add(config.getOtherOptionPrefix() + option.getDescription());

		List<String> lore = new ArrayList<>(description);
		lore.addAll(footer);
		return ItemBuilder.copy(menuItem).displayName(String.format("%s: &r%s", settingName, selectedOption.getName())).lore(lore).build();
	}

	@Override
	public List<String> getDescription() {
		return this.description;
	}

	@Override
	public List<SettingOption> getOptions() {
		return this.options;
	}

	@Override
	public @Nullable SettingOption getOption(LocalKey key) {
		return options.stream().filter(it -> it.getKey().equals(key)).findFirst().orElse(null);
	}

	private SettingOption defaultOption;

	@Override
	public SettingOption getDefaultOption() {
		return this.defaultOption;
	}

	@Override
	public void setDefaultOption(@NotNull SettingOption option) {
		if (option == null)
			throw new NullPointerException("option");
		this.defaultOption = option;
	}

	public static class OptionPlaceholder extends StringPlaceholder {

		private static final String format = "%%%s%%";

		public OptionPlaceholder(String format, Map<String, String> placeholders) {
			super(format, placeholders);
		}

		public static Builder builder() {
			return StringPlaceholder.builder().setFormat(format);
		}

		public static StringPlaceholder of(String key, String value) {
			return builder().set(key, value).build();
		}
	}
}
