package enchants.impl;

import commons.events.api.EventRegistry;
import commons.events.impl.EventSubscriber;
import commons.events.impl.bukkit.BukkitEventSubscriber;
import commons.events.impl.packet.PacketEventSubscriber;
import enchants.records.OriginEnchant;
import lombok.Getter;
import me.lucko.helper.item.ItemStackBuilder;
import net.minecraft.network.protocol.game.PacketPlayInHeldItemSlot;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

/**
 * @author vadim
 */
public enum EventsDemoEnum {

	SPEED_ENCHANT(new OriginEnchant("Speed",
									"This enchant applies the speed effect!",
									"Speed ->",
									ItemStackBuilder.of(Material.WOODEN_AXE).build(),
									new BukkitEventSubscriber<>(BlockBreakEvent.class, (event) -> {
										event.getPlayer().sendMessage("You have been given the speed effect!");
										event.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect(10*20, 1));
									})));
	@Getter private final OriginEnchant   enchant;

	EventsDemoEnum(OriginEnchant enchant) {
		this.enchant         = enchant;
	}

	public static void bind(Plugin plugin, EventRegistry events) {
		for (EventsDemoEnum value : values()) {
			value.enchant.handleEnchant().bind(plugin, events);
		}
	}

}
