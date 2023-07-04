package blocks.impl.registry;

import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import net.minecraft.core.BlockPosition;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegenerationRegistryImpl implements RegenerationRegistry {

	private final HashMap<BlockPosition, Regenable> regenableHashMap;

	public RegenerationRegistryImpl() {
		this.regenableHashMap = new HashMap<>();
	}

	@Override
	public void createRegen(Regenable block, Block original) {
		regenableHashMap.put(CraftLocation.toBlockPosition(original.getLocation()), block);
	}

	@Override
	public void deleteRegen(Block block) {
		Location location = block.getLocation();
		final BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		regenableHashMap.remove(position);
	}

	@Override
	public @NotNull HashMap<BlockPosition, Regenable> getRegenerations() {
		return this.regenableHashMap;
	}
}
