package blocks.block.builder;

import blocks.block.effects.EffectType;

public interface OriginEffectBuilder {
	EffectType<?> getEffectType();

	OriginEffectBuilder setEffectType(EffectType<?> type);
}
