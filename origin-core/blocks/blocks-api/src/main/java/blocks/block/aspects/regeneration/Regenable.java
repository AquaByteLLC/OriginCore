package blocks.block.aspects.regeneration;

import blocks.block.aspects.GeneralAspect;
import blocks.block.aspects.illusions.FakeBlock;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;

public interface Regenable extends GeneralAspect {
	Regenable setRegenTime(double regenTime);
	Regenable setFakeBlock(FakeBlock fakeBlock);
	double getRegenTime();
	FakeBlock getFakeBlock();
	RegenerationRegistry getRegistry();
}
