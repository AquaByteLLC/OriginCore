package blocks.impl.aspect.effect;

import blocks.block.aspects.effect.Effectable;
import blocks.block.builder.OriginBlockBuilder;
import blocks.block.builder.OriginEffectBuilder;

import java.util.ArrayList;
import java.util.List;

public class Effect implements Effectable {
	private final OriginBlockBuilder builder;
	private final List<OriginEffectBuilder> effectBuilders;

	public Effect(OriginBlockBuilder builder) {
		this.builder = builder;
		this.effectBuilders = new ArrayList<>();
	}

	@Override
	public OriginBlockBuilder getBuilder() {
		return this.builder;
	}

	@Override
	public List<OriginEffectBuilder> getEffects() {
		return effectBuilders;
	}

	@Override
	public Effectable addEffect(OriginEffectBuilder effectFactory) {
		effectBuilders.add(effectFactory);
		return this;
	}

	@Override
	public Effectable removeEffect(OriginEffectBuilder effectFactory) {
		effectBuilders.remove(effectFactory);
		return this;
	}
}
