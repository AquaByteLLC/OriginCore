package enchants.impl.type;

import commons.events.api.EventRegistry;
import commons.events.impl.bukkit.BukkitEventSubscriber;
import enchants.EnchantAPI;
import enchants.builder.factory.OriginEnchantFactory;
import enchants.item.EnchantedItem;
import me.lucko.helper.text3.Text;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantTypes {

	private static final JavaPlugin plugin = EnchantAPI.get().getInstance(JavaPlugin.class);
	public static final String SPEED_ENCHANT_NAME = "Speed";
	public static final NamespacedKey SPEED_ENCHANT_KEY = new NamespacedKey(plugin, SPEED_ENCHANT_NAME);

	public static final String EXPLOSIVE_ENCHANT_NAME = "EXPLOSIVE";
	public static final NamespacedKey EXPLOSIVE_ENCHANT_KEY = new NamespacedKey(plugin, EXPLOSIVE_ENCHANT_NAME);

	public EnchantTypes(JavaPlugin plugin, EventRegistry registry) {
		OriginEnchantFactory.create(SPEED_ENCHANT_NAME).build(plugin, registry, new BukkitEventSubscriber<>(BlockBreakEvent.class, event -> {
			final ItemStack playersItem = event.getPlayer().getInventory().getItemInMainHand();

			if (playersItem.getType().isAir()) return;

			final EnchantedItem item = new EnchantedItem(playersItem);

			if (item.activate(SPEED_ENCHANT_KEY)) {
				event.getPlayer().sendMessage(Text.colorize("&b&lSPEEEEED!"));
			}
		}));

		OriginEnchantFactory.create(EXPLOSIVE_ENCHANT_NAME).build(plugin, registry, new BukkitEventSubscriber<>(BlockBreakEvent.class, event -> {

		}));
	}
}


