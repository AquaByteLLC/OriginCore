package commons.conf;

import me.vadim.util.conf.ResourceProvider;
import org.bukkit.inventory.ItemStack;


/**
 * @author vadim
 */
public class CommonsConfig extends BukkitConfig {

	public CommonsConfig(ResourceProvider resourceProvider) {
		super("commons.yml", resourceProvider);
	}

	public ItemStack getMenuNext() {
		return getItem("generic_paged_menu.button.next");
	}

	public ItemStack getMenuBack() {
		return getItem("generic_paged_menu.button.back");
	}

	public ItemStack getMenuDone() {
		return getItem("generic_paged_menu.button.done");
	}

	public ItemStack getGensSettingsIcon() {
		return getItem("global_settings_menu.gens.item");
	}

}
