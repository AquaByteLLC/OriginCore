package api.section;

import api.registry.SettingsRegistry;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface SettingSection {
	String getName();

	ItemStack getMenuItem();

	List<String> getDescription();

	SettingsRegistry getRegistry();
}
