package settings.setting;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import settings.setting.key.GlobalKey;
import settings.setting.key.KeyedSetting;
import settings.setting.key.SettingKey;

import java.util.List;
import java.util.Map;

public interface SettingSection extends KeyedSetting {

	@Override
	GlobalKey getKey();

	String getName();

	ItemStack getMenuItem();

	List<String> getDescription();

	void createSetting(Setting setting);

	void deleteSetting(Setting setting);

	@Nullable Setting querySetting(GlobalKey key);

	@NotNull Map<Setting, SettingOption> getDefaultSettings();

}
