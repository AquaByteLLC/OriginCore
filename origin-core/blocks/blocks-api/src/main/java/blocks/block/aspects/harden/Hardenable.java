package blocks.block.aspects.harden;

import blocks.block.aspects.GeneralAspect;

public interface Hardenable extends GeneralAspect {
	Hardenable setHardnessMultiplier(double hardnessMultiplier);
	double getHardnessMultiplier();
}
