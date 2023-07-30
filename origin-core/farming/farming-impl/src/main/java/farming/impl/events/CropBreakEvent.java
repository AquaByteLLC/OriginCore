package farming.impl.events;

import blocks.BlocksAPI;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.data.account.BlockAccount;
import blocks.impl.events.BreakEvent;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import commons.hologram.InterpolatedHologram;
import commons.interpolation.impl.InterpolationType;
import commons.math.MathUtils;
import me.lucko.helper.serialize.Position;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class CropBreakEvent implements Listener {

	private final EventRegistry eventRegistry;
	private final BlocksPlugin plugin;

	public CropBreakEvent(BlocksPlugin plugin, EventRegistry eventRegistry) {
		this.eventRegistry = eventRegistry;
		this.plugin = plugin;
		this.eventRegistry.subscribeAll(this);
	}

	@Subscribe
	void cropBreakEvent(BlockBreakEvent event) {
		final Block block = event.getBlock();
		final BlockData blockData = event.getBlock().getBlockData();
		final Player player = event.getPlayer();
		final BlockAccount account = BlocksPlugin.get().getInstance(BlocksPlugin.class).getAccountStorage().getAccount(player);
		final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();

		if (blockData instanceof Ageable ageable) {
			if (BlocksAPI.inRegion(block.getLocation())) {
				if (regenerationRegistry.getRegenerations().containsKey(CraftLocation.toBlockPosition(block.getLocation()))) {
					event.setCancelled(true);
					return;
				}
				if (ageable.getAge() == ageable.getMaximumAge()) {
					new BreakEvent("farming", block, event.getPlayer(), false).callEvent();
					final Position position = Position.of(block.getLocation().clone().set(block.getX(), block.getY() + 1, block.getZ()));
					final float xIncrementation = MathUtils.random(-3.5f, 3.5f);
					final float yIncrementation = 2.7f;
					final float zIncrementation = MathUtils.random(-3.5f, 3.5f);

					final InterpolatedHologram hologram = new InterpolatedHologram(block, position,
							StringPlaceholder.of("{test}", String.valueOf(zIncrementation)),"zIncr: {test}");

					hologram.create(player, 27, 20, xIncrementation, yIncrementation, zIncrementation, true, true, true, InterpolationType.circle);

					event.setCancelled(true);
				}
			}
		}
	}
}
