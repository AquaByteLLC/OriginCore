package blocks.block.aspects.illusions;

import blocks.block.aspects.GeneralAspect;
import blocks.block.aspects.illusions.registry.IllusionRegistry;
import blocks.block.aspects.location.BlockLocatable;
import blocks.block.aspects.overlay.Overlayable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;

public interface FakeBlock extends GeneralAspect {
	FakeBlock setProjectedBlockData(BlockData fakeData);
	BlockData getProjectedBlockData();
	FallingBlock spawn();
	void despawn();
	Overlayable getOverlay();
	BlockLocatable getLocatable();
	FakeBlock setOverlay(Overlayable overlay);
	FakeBlock setLocatable(BlockLocatable locatable);
	IllusionRegistry getRegistry();
}
