package settings;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import settings.setting.Setting;
import settings.setting.SettingOption;
import settings.setting.key.LocalKey;

/**
 * Settings enums should implement this interface as it provides convenience for callers.
 * @author vadim
 */
public interface EnumeratedSetting {

	/**
	 * Constant predicate that returns {@code true}.
	 */
	static <T> boolean always(T __) { return true; }

	/**
	 * Constant predicate that returns {@code false}.
	 */
	static <T> boolean never(T __) { return false; }

	@NotNull Setting getSetting();

	static void nonBinarySetting() {
		throw new UnsupportedOperationException("non-binary setting");
	}

	/**
	 * Check if a boolean option is enabled. If this option is not a boolean option then this method should call {@link #nonBinarySetting()}.
	 */
	boolean isEnabled(Player player);

	/**
	 * Shortcut to check if an option is selected based on its {@linkplain SettingOption#getKey() key}.
	 */
	boolean isSelected(LocalKey option, Player player);

	/**
	 * @return the currently selected option for this setting
	 */
	@NotNull SettingOption getOption(Player player);

}
