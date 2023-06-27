package blocks.block.factory;

import blocks.block.aspects.drop.Dropable;
import blocks.block.aspects.effect.Effectable;
import blocks.block.aspects.harden.Hardenable;
import blocks.block.aspects.illusions.FakeBlock;
import blocks.block.aspects.location.BlockLocatable;
import blocks.block.aspects.overlay.Overlayable;
import blocks.block.aspects.regeneration.Regenable;

public interface AspectFactory {
	Dropable newDropable();
	Effectable newEffectable();
	Hardenable newHardenable();
	Regenable newRegenable();
	Overlayable newOverlayable();
	BlockLocatable newLocatable();
	FakeBlock newFakeBlock();
}
