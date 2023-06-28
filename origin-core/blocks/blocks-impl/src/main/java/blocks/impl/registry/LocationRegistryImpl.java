package blocks.impl.registry;

import blocks.block.aspects.location.registry.BlockLocationRegistry;
import blocks.block.builder.AspectHolder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LocationRegistryImpl implements BlockLocationRegistry {

	private final HashMap<Location, AspectHolder> locatableBlocks;

	public LocationRegistryImpl() {
		this.locatableBlocks = new HashMap<>();
	}

	@Override
	public void createBlock(AspectHolder editor, Location block) {
		locatableBlocks.put(block, editor);
	}

	@Override
	public void deleteBlock(AspectHolder editor, Location block) {
		locatableBlocks.remove(block);
	}

	@Override
	public @NotNull HashMap<Location, AspectHolder> getBlocks() {
		return this.locatableBlocks;
	}
}
