package blocks.impl.aspect.harden;

import blocks.block.aspects.AspectType;
import blocks.block.aspects.BlockAspect;
import blocks.block.aspects.harden.Hardenable;
import blocks.block.builder.AspectHolder;
import blocks.impl.aspect.BaseAspect;

public class Harden extends BaseAspect implements Hardenable {

	private double hardnessMultiplier;

	public Harden(AspectHolder editor) {
		super(editor, AspectType.HARDENABLE);
	}

	@Override
	public void setHardnessMultiplier(double hardnessMultiplier) {
		this.hardnessMultiplier = hardnessMultiplier;
	}

	@Override
	public double getHardnessMultiplier() {
		return this.hardnessMultiplier;
	}

	@Override
	public BlockAspect copy(AspectHolder newHolder) {
		Harden harden = new Harden(newHolder);
		harden.hardnessMultiplier = hardnessMultiplier;
		return harden;
	}

}
