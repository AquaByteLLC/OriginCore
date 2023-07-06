package settings.impl.builder;

import api.Setting;
import api.option.SettingsOption;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SettingsBuilder implements api.builder.SettingsBuilder {

	private String name;
	private ItemStack stack;
	private List<String> description;
	private List<SettingsOption> options;
	private SettingsOption defaultOption;

	@Override
	public api.builder.SettingsBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public api.builder.SettingsBuilder setMenuItem(ItemStack stack) {
		this.stack = stack;
		return this;
	}

	@Override
	public api.builder.SettingsBuilder setDescription(List<String> description) {
		this.description = description;
		return this;
	}

	@Override
	public api.builder.SettingsBuilder setOptions(List<SettingsOption> options) {
		this.options = options;
		return this;
	}

	@Override
	public api.builder.SettingsBuilder setDefaultOption(SettingsOption option) {
		this.defaultOption = option;
		return this;
	}

	@Override
	public Setting build() {
		final Setting setting = new settings.impl.setting.Setting(name, stack, description);
		setting.setDefaultOption(defaultOption);
		setting.getOptions().addAll(options);
		return setting;
	}
}
