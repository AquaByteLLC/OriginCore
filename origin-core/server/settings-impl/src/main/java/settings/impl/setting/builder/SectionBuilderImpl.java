package settings.impl.setting.builder;

import me.vadim.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import settings.impl.setting.MenuSection;
import settings.impl.setting.key.GKey;
import settings.setting.Setting;
import settings.setting.SettingSection;
import settings.setting.builder.SectionBuilder;

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
		if(menuItem == null && name == null && description == null)
			throw new IllegalStateException("Call some of the builder methods first.");

		if(menuItem == null)
			menuItem = ItemBuilder.create(Material.STONE).build();
		if(name == null || name.isBlank())
			name = menuItem.getItemMeta().getDisplayName();
		if(description == null || description.isEmpty() || description.stream().allMatch(String::isBlank))
			description = menuItem.getLore();

		if(name.isBlank())
			throw new IllegalStateException("Can't build section because no name was provided.");

		SettingSection section = new MenuSection(GKey.convert(name), name, menuItem, description);
		inital.forEach(section::createSetting);
		return section;
	}

}
