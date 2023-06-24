package enchants.impl.item;

import enchants.item.EnchantFactory;
import enchants.EnchantKey;
import enchants.item.EnchantBuilder;
import enchants.item.EnchantedItem;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public class OriginEnchantFactory implements EnchantFactory {

	@Override
	public EnchantedItem newEnchantedItem(Consumer<ItemStackBuilder> builder) {
		ItemStack stack = ItemStackBuilder.of(Material.STONE_AXE).build();
		builder.accept(ItemStackBuilder.of(stack));
		final EnchantedItem item = new EnchantedItemImpl(stack);
		item.makeEnchantable();
		return item;
	}

	@Override
	public EnchantedItem wrapItemStack(ItemStack item) {
		return new EnchantedItemImpl(item);
	}

	@Override
	public EnchantBuilder newEnchantBuilder(EnchantKey key) {
		return new OriginEnchantBuilder(key);
	}

	private static final NamespacedKey reqKey = new NamespacedKey("enchants", "_enchantable");
	private static final String reqValue = "isEnchantable";

	@Override
	public NamespacedKey getEnchantableKey() {
		return reqKey;
	}

	@Override
	public boolean canEnchant(ItemStack item) {
		return item.hasItemMeta() && canEnchant(item.getItemMeta().getPersistentDataContainer());
	}

	@Override
	@SuppressWarnings("DataFlowIssue")
	public boolean canEnchant(PersistentDataContainer container) {
		return container.has(reqKey) && container.get(reqKey, PersistentDataType.STRING).equals(reqValue);
	}

	@Override
	public void setCanEnchant(ItemStack item, boolean canEnchant) {
		if(!item.hasItemMeta())
			return;
		item.editMeta(meta -> setCanEnchant(meta.getPersistentDataContainer(), canEnchant));
	}

	@Override
	public void setCanEnchant(PersistentDataContainer container, boolean canEnchant) {
		if(canEnchant)
			container.set(reqKey, PersistentDataType.STRING, reqValue);
		else
			container.remove(reqKey);
	}

}
