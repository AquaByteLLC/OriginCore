package settings.setting;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import settings.setting.key.GlobalKey;
import settings.setting.key.KeyedSetting;
import settings.setting.key.LocalKey;

import java.util.List;

public interface Setting extends KeyedSetting {

	@Override
	GlobalKey getKey();

	String getName();

	ItemStack getMenuItem(SettingOption selectedOption);

	List<String> getDescription();

	List<SettingOption> getOptions();

	@Nullable SettingOption getOption(LocalKey key);

	SettingOption getDefaultOption();

	void setDefaultOption(@NotNull SettingOption option);

}
