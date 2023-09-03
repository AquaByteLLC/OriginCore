package tools.impl.conf;

import commons.conf.BukkitConfig;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import org.bukkit.inventory.ItemStack;

public class Config extends BukkitConfig {

	public Config(ResourceProvider resourceProvider) {
		super("config.yml", resourceProvider);
		setDefaultTemplate();
	}

	// General

	public ItemStack getAugmentsItem() {
		return getItem("general_menu.augments_item");
	}

	public ItemStack getSkinsItem() {
		return getItem("general_menu.skins_item");
	}

	public ItemStack getEnchantsItem() {
		return getItem("general_menu.enchants_item");
	}


	// Enchant

	public UnformattedItem getEnchantMenuUpgrade() {
		return getUnformatted("enchant_menu.button.upgrade");
	}

	public PlaceholderMessage getEnchantMenuTitle() {
		return getConfigurationAccessor().getObject("enchant_menu").getPlaceholder("title");
	}

	// Augment

	public BukkitConfig.UnformattedItem getAugmentMenuApplied() {
		return getUnformatted("augment_menu.button.applied");
	}

	public BukkitConfig.UnformattedItem getAugmentMenuLocked() {
		return getUnformatted("augment_menu.button.locked");
	}

	public BukkitConfig.UnformattedItem getAugmentMenuNotApplied() {
		return getUnformatted("augment_menu.button.notApplied");
	}

	public PlaceholderMessage getAugmentMenuTitle() {
		return getConfigurationAccessor().getObject("augment_menu").getPlaceholder("title");
	}

	// Skin

	public PlaceholderMessage getSkinMenuTitle() {
		return getConfigurationAccessor().getObject("skin_menu").getPlaceholder("title");
	}

}
