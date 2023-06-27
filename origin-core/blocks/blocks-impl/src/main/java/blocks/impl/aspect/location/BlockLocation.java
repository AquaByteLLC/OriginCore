package blocks.impl.aspect.location;

import blocks.block.aspects.location.BlockLocatable;
import blocks.block.aspects.location.registry.BlockLocationRegistry;
import blocks.block.builder.OriginBlockBuilder;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockLocation implements BlockLocatable {
	private Location location;
	private Block block;
	private final OriginBlockBuilder builder;
	private final BlockLocationRegistry registry;

	public BlockLocation(OriginBlockBuilder builder) {
		this.builder = builder;
		registry = null;
	}

	@Override
	public OriginBlockBuilder getBuilder() {
		return builder;
	}

	@Override
	public Location getBlockLocation() {
		return this.location;
	}

	@Override
	public BlockLocatable setBlockLocation(Location location) {
		this.location = location;
		return this;
	}

	@Override
	public BlockLocatable setBlock(Block block) {
		this.block = block;
		return this;
	}

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public void registerLocation() {
		registry.createBlock(builder, this.location);
	}

	@Override
	public void unregisterLocation() {
		registry.deleteBlock(builder, this.location);
	}
}
