package settings.impl.setting;

import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import me.vadim.util.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import settings.Setting;
import settings.option.SettingsOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantValue")
public class MenuSetting implements Setting {

	private final String settingName;
	private ItemStack menuItem;
	private final List<SettingsOption> options;
	private final List<String> description;

	public MenuSetting(String settingName, ItemStack menuItem, List<String> description) {
		this.settingName = settingName;
		this.options = new ArrayList<>();

		this.menuItem = ItemBuilder.copy(menuItem)
				.displayName(settingName)
				.lore(description)
				.build();

		this.description = description;
	}

	@Override
	public String getName() {
		return this.settingName;
	}

	@Override
	public ItemStack getMenuItem() {
		return this.menuItem;
	}

	@Override
	public List<String> getDescription() {
		return this.description;
	}

	@Override
	public List<SettingsOption> getOptions() {
		return this.options;
	}

	private SettingsOption defaultOption;

	@Override
	public SettingsOption getDefaultOption() {
		return this.defaultOption;
	}

	@Override
	public void setDefaultOption(@NotNull SettingsOption option) {
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
