package blocks.impl.registry;

import blocks.block.aspects.AspectType;
import blocks.block.aspects.projection.Projectable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionFactory;
import blocks.block.illusions.IllusionRegistry;
import blocks.block.illusions.IllusionsAPI;
import me.lucko.helper.Schedulers;
import net.minecraft.core.BlockPosition;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegenerationRegistryImpl implements RegenerationRegistry {

	private final HashMap<BlockPosition, Regenable> regenableHashMap;
	private final IllusionRegistry illusionRegistry;
	private final IllusionFactory illusionFactory;
	private final JavaPlugin plugin;

	public RegenerationRegistryImpl(JavaPlugin plugin, IllusionsAPI illusions) {
		this.regenableHashMap = new HashMap<>();
		this.illusionRegistry = illusions.registry();
		this.illusionFactory = illusions.factory();
		this.plugin = plugin;
	}

	@Override
	public void createRegen(Regenable block, Block original, Player player, long end) {
/*
		Location location = null; // = block.getLocation();

		FakeBlock fake;

		AspectHolder originBlock;

		Projectable projectable = (Projectable) originBlock.getAspects().get(AspectType.PROJECTABLE);

		illusionFactory.newFakeBlock(null, projectable.getProjectedBlockData());

		BlockOverlay overlay = null; // = illusionFactory.newXOverlay(...);
		fake = illusionFactory.newOverlayedBlock(location, overlay);

		BlockData fakeData = null; // = ...;
		fake = illusionFactory.newFakeBlock(location, fakeData);

		//using it
		illusionRegistry.register(fake);

		//save somewhere


		//done with it
		illusionRegistry.unregister(fake);*/
		Location location = block.getFakeBlock().getBlockLocation();
		final BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());

		regenableHashMap.put(position, block);
		illusionRegistry.register(block.getFakeBlock());
		player.sendBlockChange(location, block.getFakeBlock().getProjectedBlockData());

		Schedulers.bukkit().runTaskTimer(plugin, bukkitTask -> {
			if (System.currentTimeMillis() >= end) {
				BlockData blockData = original.getBlockData();
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
				player.sendBlockChange(location, original.getBlockData());
				illusionRegistry.unregister(block.getFakeBlock());
				bukkitTask.cancel();
			}
		}, 0, 5);
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
