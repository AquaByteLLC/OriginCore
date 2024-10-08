package enderchests.impl.conf;

import commons.conf.BukkitConfig;
import me.vadim.util.conf.ResourceProvider;
import me.vadim.util.conf.bukkit.wrapper.EffectGroup;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import me.vadim.util.conf.wrapper.impl.UnformattedMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Config extends BukkitConfig {

	public static final int CHEST_SIZE = 9 * 3;

	@SuppressWarnings("DataFlowIssue")
	public Config(ResourceProvider resourceProvider) {
		super("config.yml", resourceProvider);
		setDefaultTemplate();
		yaml.getDefaults().set("effects", null); // remove defaults for effects so it won't auto-replace deleted fields
	}

	public int getDefaultsSlots() {
		return getConfigurationAccessor().getInt("default_slots_per_net");
	}

	public PlaceholderMessage getLinkedInventoryTitle() {
		return getConfigurationAccessor().getPlaceholder("linked_inventory.title");
	}

	public PlaceholderMessage getSelectColorMenuTitle() {
		return getConfigurationAccessor().getPlaceholder("select_color_menu.title");
	}

	public List<PlaceholderMessage> getSlotLimitLore() {
		List<PlaceholderMessage> lore = new ArrayList<>();
		for (String line : getConfigurationAccessor().getStringArray("select_color_menu.slot_limit_lore"))
			lore.add(new UnformattedMessage(line));
		return lore;
	}

	public UnformattedItem getSelectColorItem() {
		return getUnformatted("select_color_menu.colored_item");
	}

	public EffectGroup getOpenEffect() {
		return getEffect("effects.open");
	}

	public EffectGroup getShutEffect() {
		return getEffect("effects.shut");
	}

	public static final NamespacedKey echest_key = new NamespacedKey("enderchests", "chest.item");
	public static final String echest_value = "unplaced_chest";

	public static boolean isEnderChestItem(ItemStack item) {
		if (item == null)
			return false;
		if (!item.hasItemMeta())
			return false;

		PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
		return pdc.has(echest_key) && echest_value.equals(pdc.get(echest_key, PersistentDataType.STRING));
	}

	public ItemStack getEnderChestItem() {
		ItemStack item = getItem("item.ender_chest");
		item.editMeta(meta -> {
			meta.getPersistentDataContainer().set(echest_key, PersistentDataType.STRING, echest_value);
		});
		return item;
	}

	String[] allowed;

	@Override
	public void reload() {
		super.reload();
		allowed = null;
	}

	public String[] getAllowedChestSounds() {
		if (allowed == null) {
			allowed = new String[2];
			Sound sound;

			sound = getOpenEffect().sound.sound;
			if (sound != null)
				allowed[0] = "minecraft." + sound.key().value();

			sound = getShutEffect().sound.sound;
			if (sound != null)
				allowed[1] = "minecraft." + sound.key().value();
		}

		return allowed;
	}

}
