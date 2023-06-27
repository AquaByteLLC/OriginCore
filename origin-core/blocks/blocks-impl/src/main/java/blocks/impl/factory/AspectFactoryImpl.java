package blocks.impl.factory;

import blocks.block.aspects.drop.Dropable;
import blocks.block.aspects.effect.Effectable;
import blocks.block.aspects.harden.Hardenable;
import blocks.block.aspects.illusions.FakeBlock;
import blocks.block.aspects.location.BlockLocatable;
import blocks.block.aspects.overlay.Overlayable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.builder.OriginBlockBuilder;
import blocks.block.factory.AspectFactory;
import blocks.impl.aspect.drop.Drop;
import blocks.impl.aspect.effect.Effect;
import blocks.impl.aspect.harden.Harden;
import blocks.impl.aspect.illusion.IllusionBlock;
import blocks.impl.aspect.location.BlockLocation;
import blocks.impl.aspect.overlay.Overlay;
import blocks.impl.aspect.regen.Regeneration;

public class AspectFactoryImpl implements AspectFactory {

	private final OriginBlockBuilder builder;
	public AspectFactoryImpl(OriginBlockBuilder builder) {
		this.builder = builder;
	}

	@Override
	public Dropable newDropable() {
		return new Drop(builder);
	}
	@Override
	public Effectable newEffectable() {
		return new Effect(builder);
	}
	@Override
	public Hardenable newHardenable() {
		return new Harden(builder);
	}
	@Override
	public Regenable newRegenable() {
		return new Regeneration(builder);
	}
	@Override
	public Overlayable newOverlayable() {
		return new Overlay(builder);
	}
	@Override
	public BlockLocatable newLocatable() {
		return new BlockLocation(builder);
	}
	@Override
	public FakeBlock newFakeBlock() {
		return new IllusionBlock(builder, builder.getFactory());
	}
}
