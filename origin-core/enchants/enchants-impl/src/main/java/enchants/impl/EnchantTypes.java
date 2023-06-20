package enchants.impl;

import commons.events.api.EventRegistry;
import commons.events.impl.bukkit.BukkitEventSubscriber;
import enchants.item.EnchantedItem;
import enchants.records.OriginEnchant;
import lombok.Getter;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

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
				final EnchantedItem item = new EnchantedItem(event.getPlayer().getItemInUse());

				if (item.hasEnchant(NamespacedKey.minecraft("Speed"))) {
					if (item.getLevel(NamespacedKey.minecraft("Speed")) > 4) {
						System.out.println("Greater than 4 for lvl");
					} else if (item.getLevel(NamespacedKey.minecraft("Speed")) < 4) {
						System.out.println("Less than 4 for lvl");
					}
				}
			})));
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
