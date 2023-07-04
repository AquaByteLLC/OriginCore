package blocks.impl.factory;

import blocks.block.aspects.drop.Dropable;
import blocks.block.aspects.effect.Effectable;
import blocks.block.aspects.harden.Hardenable;
import blocks.block.aspects.overlay.Overlayable;
import blocks.block.aspects.projection.Projectable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.factory.AspectFactory;
import blocks.impl.aspect.drop.Drop;
import blocks.impl.aspect.effect.Effect;
import blocks.impl.aspect.harden.Harden;
import blocks.impl.aspect.overlay.Overlay;
import blocks.impl.aspect.projection.Projection;
import blocks.impl.aspect.regen.Regeneration;
import blocks.impl.builder.OriginBlock;

public class AspectFactoryImpl implements AspectFactory {

	private final OriginBlock block;

	public AspectFactoryImpl(OriginBlock block) {
		this.block = block;
	}

	@Override
	public Dropable newDropable() {
		return new Drop(block);
	}

	@Override
	public Effectable newEffectable() {
		return new Effect(block);
	}

	@Override
	public Hardenable newHardenable() {
		return new Harden(block);
	}

	@Override
	public Regenable newRegenable() {
		return new Regeneration(block);
	}

	@Override
	public Overlayable newOverlayable() {
		return new Overlay(block);
	}

	@Override
	public Projectable newProjectable() {
		return new Projection(block);
	}
}
