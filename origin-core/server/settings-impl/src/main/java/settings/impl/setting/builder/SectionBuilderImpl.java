package settings.impl.setting.builder;

import org.bukkit.inventory.ItemStack;
import settings.impl.setting.key.GKey;
import settings.setting.Setting;
import settings.setting.key.GlobalKey;
import settings.setting.builder.SectionBuilder;
import settings.impl.setting.MenuSection;
import settings.setting.SettingSection;

import java.util.ArrayList;
import java.util.List;

public class SectionBuilderImpl implements SectionBuilder {

	private String name;
	private ItemStack menuItem;
	private List<String> description;
	private final List<Setting> inital = new ArrayList<>();

	SectionBuilderImpl() { }

	@Override
	public SectionBuilder setMenuItem(ItemStack menuItem) {
		this.menuItem = menuItem;
		return this;
	}

	@Override
	public SectionBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public SectionBuilder setDescription(String... description) {
		this.description = List.of(description);
		return this;
	}

	@Override
	public SectionBuilder addSetting(Setting setting) {
		this.inital.add(setting);
		return this;
	}

	@Override
	public SettingSection build() {
		SettingSection section = new MenuSection(GKey.convert(name), name, menuItem, description);
		inital.forEach(section::createSetting);
		return section;
	}

}
