package tools.impl;

import com.mojang.datafixers.util.Pair;
import commons.events.impl.impl.DetachedSubscriber;
import enchants.impl.EnchantTypes;
import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.builder.ToolBuilder;

import java.util.List;

public class ToolsPlugin extends ExtendedJavaPlugin {

	@Getter
	private static ToolsPlugin plugin;
	private DetachedSubscriber<PlayerJoinEvent> playerJoinEventDetachedSubscriber;

	private static final ToolBuilder builder = new ToolBuilder(Material.WOODEN_AXE)
			.lore(List.of("&eTesting the lore", "&f{blocks}"))
			.displayName("&eTesting name")
			.makeEnchantable()
			.setBaseCustomEnchants(Pair.of(EnchantTypes.SPEED, 2))
			.createCustomDataUpdate("testing", "blocks", PersistentDataType.INTEGER, 0, (taskDataBuilder, stack) -> {
				taskDataBuilder.consumeEvent(BlockBreakEvent.class, ((context, event) -> {
					final ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
					final int current = ToolBuilder.getData(item, "testing", "blocks", PersistentDataType.INTEGER);
					ToolBuilder.createCustomData(item, "testing", "blocks", PersistentDataType.INTEGER, (current + 1));
					ToolBuilder.updateItem(item, List.of("&eTesting the lore", "&f{blocks}"), "&eTesting name");
				}));
			}).bindModelData(1, (taskDataBuilder, stack) -> {
				taskDataBuilder.consumeTaskTimer(10, 0, ($) -> {
					if (taskDataBuilder.compare(stack)) {
						System.out.println("RUNNING TASK!");
					}
				});

				taskDataBuilder.consumeEvent(BlockBreakEvent.class, ((context, event) -> {
					if (taskDataBuilder.compare(stack)) {
						event.getPlayer().sendMessage("Events working for data!");
					}
				}));

			});

	@Override
	protected void enable() {
		plugin = this;
		playerJoinEventDetachedSubscriber = new DetachedSubscriber<>(PlayerJoinEvent.class, ((context, event) -> {
		}));

	}
}
