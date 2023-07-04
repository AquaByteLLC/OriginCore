package blocks.impl.registry;

import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.impl.event.OriginRegenerationEvent;
import net.minecraft.core.BlockPosition;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegenerationRegistryImpl implements RegenerationRegistry {

	private final HashMap<BlockPosition, Regenable> regenableHashMap;

	public RegenerationRegistryImpl() {
		this.regenableHashMap = new HashMap<>();
	}

	@Override
	public void createRegen(Regenable block, Block original, Player player, long end) {
		final Location location = block.getFakeBlock().getBlockLocation();
		final BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		final OriginRegenerationEvent event = new OriginRegenerationEvent(block, player, original, end);
		regenableHashMap.put(position, block);
		event.callEvent();
		//	illusionRegistry.register(block.getFakeBlock(), player);
		//	player.sendBlockChange(location, block.getFakeBlock().getProjectedBlockData());
		/*
		Schedulers.bukkit().runTaskTimer(plugin, bukkitTask -> {
			if (System.currentTimeMillis() >= end) {
				final BlockData blockData = original.getBlockData();

				if (blockData instanceof Ageable ageable) {
					Schedulers.bukkit().runTaskTimer(plugin, runnable -> {
						if (!(ageable.getAge() == ageable.getMaximumAge())) {
							ageable.setAge(ageable.getAge() + 1);
							Projectable projectable = (Projectable) block.getEditor().getAspects().get(AspectType.PROJECTABLE);
							projectable.setProjectedBlockData(ageable);
							FakeBlock newFakeBlock = projectable.toFakeBlock(location);
							illusionRegistry.unregister(block.getFakeBlock());
							illusionRegistry.register(newFakeBlock);
							player.sendBlockChange(location, newFakeBlock.getProjectedBlockData());
						} else {
							runnable.cancel();
						}
					}, 10, 10);
				}

				deleteRegen(block);
				illusionRegistry.unregister(block.getFakeBlock());
				player.sendBlockChange(location, blockData);
				bukkitTask.cancel();
			}
		}, 0, 5);


		 */
	}

	@Override
	public void deleteRegen(Regenable block) {
		Location location = block.getFakeBlock().getBlockLocation();
		final BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		regenableHashMap.remove(position);
	}

	@Override
	public @NotNull HashMap<BlockPosition, Regenable> getRegenerations() {
		return this.regenableHashMap;
	}
}
