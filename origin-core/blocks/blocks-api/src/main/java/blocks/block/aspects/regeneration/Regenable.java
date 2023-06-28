package blocks.block.aspects.regeneration;

import blocks.block.aspects.BlockAspect;
import blocks.block.illusions.FakeBlock;

public interface Regenable extends BlockAspect {

	double getRegenTime();

	void setRegenTime(double regenTime);

	FakeBlock getFakeBlock();

	void setFakeBlock(FakeBlock fakeBlock);

}
