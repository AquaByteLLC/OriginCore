package api;

import api.option.SettingsOption;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Setting {
	String getName();

	ItemStack getMenuItem();

	List<String> getDescription();

	List<SettingsOption> getOptions();

	SettingsOption getDefaultOption();

	void setDefaultOption(SettingsOption option);

}
