package settings;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import settings.option.SettingsOption;

import java.util.List;

public interface Setting {

	String getName();

	ItemStack getMenuItem();

	List<String> getDescription();

	List<SettingsOption> getOptions();

	SettingsOption getDefaultOption();


	void setDefaultOption(@NotNull SettingsOption option);

}
