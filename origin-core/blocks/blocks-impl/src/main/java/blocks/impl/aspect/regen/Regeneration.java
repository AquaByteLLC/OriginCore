package blocks.impl.aspect.regen;

import blocks.block.aspects.AspectType;
import blocks.block.aspects.BlockAspect;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.builder.AspectHolder;
import blocks.block.illusions.FakeBlock;
import blocks.impl.aspect.BaseAspect;

public class Regeneration extends BaseAspect implements Regenable {
	private double regenTime;
	private FakeBlock fakeBlock;

	public Regeneration(AspectHolder editor) {
		super(editor, AspectType.REGENABLE);
	}

	@Override
	public void setRegenTime(double regenTime) {
		this.regenTime = regenTime;
	}

	@Override
	public void setFakeBlock(FakeBlock fakeBlock) {
		this.fakeBlock = fakeBlock;
	}

	@Override
	public double getRegenTime() {
		return this.regenTime;
	}

	@Override
	public FakeBlock getFakeBlock() {
		return this.fakeBlock;
	}

	@Override
	public BlockAspect copy(AspectHolder newHolder) {
		Regeneration regeneration = new Regeneration(newHolder);
		regeneration.regenTime = regenTime;
		return regeneration;
	}

}
