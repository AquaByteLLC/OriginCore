package blocks.impl.registry;

import blocks.block.aspects.location.registry.BlockLocationRegistry;
import blocks.block.builder.OriginBlockBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LocationRegistryImpl implements BlockLocationRegistry {

	private final HashMap<Location, OriginBlockBuilder> locatableBlocks;

	public LocationRegistryImpl() {
		this.locatableBlocks = new HashMap<>();
	}

	@Override
	public void createBlock(OriginBlockBuilder builder, Location block) {
		locatableBlocks.put(block, builder);
	}

	@Override
	public void deleteBlock(OriginBlockBuilder builder, Location block) {
		locatableBlocks.remove(block);
	}

	@Override
	public @NotNull HashMap<Location, OriginBlockBuilder> getBlocks() {
		return this.locatableBlocks;
	}
}
