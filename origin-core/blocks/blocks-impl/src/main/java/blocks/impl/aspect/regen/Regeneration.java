package blocks.impl.aspect.regen;

import blocks.BlocksAPI;
import blocks.block.aspects.illusions.FakeBlock;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.builder.OriginBlockBuilder;

public class Regeneration implements Regenable {
	private final OriginBlockBuilder builder;
	private double regenTime;
	private FakeBlock fakeBlock;
	private final RegenerationRegistry regenerationRegistry;

	public Regeneration(OriginBlockBuilder builder) {
		this.builder = builder;
		this.regenTime = 0.0;
		this.fakeBlock = null;
		this.regenerationRegistry = BlocksAPI.getRegenerationRegistry();
	}

	@Override
	public OriginBlockBuilder getBuilder() {
		return this.builder;
	}

	@Override
	public Regenable setRegenTime(double regenTime) {
		this.regenTime = regenTime;
		return this;
	}

	@Override
	public Regenable setFakeBlock(FakeBlock fakeBlock) {
		this.fakeBlock = fakeBlock;
		return this;
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
	public RegenerationRegistry getRegistry() {
		return this.regenerationRegistry;
	}
}
