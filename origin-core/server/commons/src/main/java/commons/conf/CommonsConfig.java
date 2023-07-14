package commons.conf;

import me.vadim.util.conf.ResourceProvider;
import org.bukkit.inventory.ItemStack;


/**
 * @author vadim
 */
public class CommonsConfig extends BukkitConfig {

	public CommonsConfig(ResourceProvider resourceProvider) {
		super("commons.yml", resourceProvider);
		setDefaultTemplate();
	}

	public long getAutosaveInvervalTicks() {
		return Math.round(getConfiguration().getDouble("autosave_interval_minutes") * 60. * 20.);
	}

	public int getDelayPackTime() {
		return getConfiguration().getInt("delay-pack-sending-by");
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
	public ItemStack getFarmingSettingsIcon() {
		return getItem("global_settings_menu.farming.item");
	}

	public ItemStack getEChestsSettingsIcon() {
		return getItem("global_settings_menu.echests.item");
	}


}
