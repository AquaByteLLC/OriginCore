package blocks.impl.aspect.harden;

import blocks.block.aspects.harden.Hardenable;
import blocks.block.builder.OriginBlockBuilder;

public class Harden implements Hardenable {

	private final OriginBlockBuilder builder;
	private double hardnessMultiplier;

	public Harden(OriginBlockBuilder builder) {
		this.builder = builder;
		hardnessMultiplier = 0.0;
	}

	@Override
	public OriginBlockBuilder getBuilder() {
		return this.builder;
	}

	@Override
	public Hardenable setHardnessMultiplier(double hardnessMultiplier) {
		this.hardnessMultiplier = hardnessMultiplier;
		return this;
	}

	@Override
	public double getHardnessMultiplier() {
		return this.hardnessMultiplier;
	}
}
