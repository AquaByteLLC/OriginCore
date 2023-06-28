package blocks.block.aspects.harden;

import blocks.block.aspects.BlockAspect;

public interface Hardenable extends BlockAspect {

	void setHardnessMultiplier(double hardnessMultiplier);

	double getHardnessMultiplier();

}
