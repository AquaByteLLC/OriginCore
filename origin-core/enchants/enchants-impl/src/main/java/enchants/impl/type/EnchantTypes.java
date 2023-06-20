package enchants.impl.type;

import commons.events.api.EventRegistry;
import commons.events.impl.bukkit.BukkitEventSubscriber;
import enchants.EnchantAPI;
import enchants.item.EnchantedItem;
import enchants.records.OriginEnchant;
import lombok.Getter;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.text3.Text;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public enum EnchantTypes {

	SPEED_ENCHANT(new OriginEnchant("Speed",
			"This enchant applies the speed effect!",
			"&bSpeed &f-> &b%level%",
			ItemStackBuilder.of(Material.WOODEN_AXE).build(),
			10,
			100,
			1000,
			5,
			100,
			OriginEnchant.EnchantProgressionType.EXPONENTIAL,
			OriginEnchant.EnchantProgressionType.EXPONENTIAL,
			new BukkitEventSubscriber<>(BlockBreakEvent.class, (event) -> {
				final NamespacedKey enchantKey = new NamespacedKey(EnchantAPI.get().getInstance(JavaPlugin.class), "Speed");
				final ItemStack playersItem = event.getPlayer().getInventory().getItemInMainHand();

				if (playersItem.getType().isAir()) return;

				final EnchantedItem item = new EnchantedItem(playersItem);

				if (item.activate(enchantKey)) {
					event.getPlayer().sendMessage(Text.colorize("&b&lSPEEEEED!"));
				}
			})).addToRegistry());
	@Getter private final OriginEnchant enchant;
	EnchantTypes(OriginEnchant enchant) {
		this.enchant = enchant;
	}

	public static void bind(Plugin plugin, EventRegistry events) {
		for (EnchantTypes value : values()) {
			value.enchant.handleEnchant().bind(plugin, events);
		}
	}

}
