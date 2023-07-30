package farming.impl.events;

import blocks.BlocksAPI;
import blocks.block.aspects.AspectType;
import blocks.block.aspects.projection.Projectable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.illusions.IllusionRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.data.account.BlockAccount;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import me.lucko.helper.Schedulers;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.concurrent.atomic.AtomicInteger;

public class RegenEvent implements Listener {

	private final EventRegistry eventRegistry;
	private final BlocksPlugin plugin;

	public RegenEvent(BlocksPlugin plugin, EventRegistry eventRegistry) {
		this.eventRegistry = eventRegistry;
		this.plugin = plugin;
		this.eventRegistry.subscribeAll(this);
	}

	@Subscribe
	void regenEvent(blocks.impl.events.RegenEvent event) {
		if (!event.getCalling().equals("farming")) return;

		final BlockAccount account = plugin.getAccounts().getAccount(event.getPlayer());
		if (account == null) return;

		final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();
		final Regenable regenBlock = event.getRegenable();
		final Player player = event.getPlayer();
		final Block block = event.getBlock();
		final IllusionRegistry illusionRegistry = event.getIllusionsAPI().localRegistry(player);
		final Location location = regenBlock.getFakeBlock().getBlockLocation();
		final Projectable projectable = (Projectable) regenBlock.getEditor().getAspects().get(AspectType.PROJECTABLE);
		final Ageable ageable = ((Ageable) block.getBlockData());

		Schedulers.bukkit().runTaskLater(plugin, $ -> {
			illusionRegistry.register(regenBlock.getFakeBlock());
			System.out.println(illusionRegistry.getBlockAt(regenBlock.getFakeBlock().getBlockLocation()));
		}, 3);

		AtomicInteger atomicAge = new AtomicInteger();
		Schedulers.bukkit().runTaskTimer(plugin, bukkitTask -> {
			if (System.currentTimeMillis() >= event.getEnd()) {

				Schedulers.bukkit().runTaskTimer(plugin, runnable -> {
					if (atomicAge.get() == ageable.getMaximumAge()) {
						regenerationRegistry.deleteRegen(block);
						illusionRegistry.unregister(regenBlock.getFakeBlock());
						BlocksAPI.resetBlock(block.getLocation());
						runnable.cancel();
					}

					ageable.setAge(atomicAge.getAndIncrement());
					projectable.setProjectedBlockData(ageable);

					illusionRegistry.register(projectable.toFakeBlock(location));
				}, 10, 10);

				bukkitTask.cancel();
			}
		}, 0, 5);
	}
}
