package settings.impl.setting.builder;

import org.bukkit.inventory.ItemStack;
import settings.impl.setting.key.GKey;
import settings.setting.Setting;
import settings.setting.key.GlobalKey;
import settings.setting.builder.SettingBuilder;
import settings.impl.setting.MenuSetting;
import settings.setting.SettingOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingBuilderImpl implements SettingBuilder {

	private String name;
	private ItemStack stack;
	private List<String> description;
	private final List<SettingOption> options = new ArrayList<>();
	private int defaultOption;

	SettingBuilderImpl() { }

	@Override
	public SettingBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public SettingBuilder setMenuItem(ItemStack stack) {
		this.stack = stack;
		return this;
	}

	@Override
	public SettingBuilder setDescription(String... description) {
		this.description = List.of(description);
		return this;
	}

	@Override
	public SettingBuilder addOptions(SettingOption... options) {
		this.options.addAll(Arrays.asList(options));
		return this;
	}

	@Override
	public SettingBuilder setDefaultOption(int optionIndex) {
		this.defaultOption = optionIndex;
		return this;
	}

	@Override
	public Setting build() {
		final Setting setting = new MenuSetting(GKey.convert(name), name, stack, description);

		if (options.isEmpty())
			throw new IllegalArgumentException("call #addOptions(...) first!");
		if (defaultOption < 0 || defaultOption >= options.size())
			throw new IndexOutOfBoundsException("default optionIndex " + defaultOption + " is out of bounds for length " + options.size());

		setting.setDefaultOption(options.get(defaultOption));
		setting.getOptions().addAll(options);
		return setting;
	}

}
