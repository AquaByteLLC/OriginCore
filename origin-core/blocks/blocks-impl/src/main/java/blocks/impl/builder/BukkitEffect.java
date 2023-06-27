package blocks.impl.builder;

import blocks.block.builder.OriginEffectBuilder;
import blocks.block.effects.EffectType;

public class BukkitEffect implements OriginEffectBuilder {
	private EffectType<?> effectType;

	public BukkitEffect(EffectType<?> effectType) {
		this.effectType = effectType;
	}

	@Override
	public EffectType<?> getEffectType() {
		return this.effectType;
	}

	@Override
	public OriginEffectBuilder setEffectType(EffectType<?> type) {
		this.effectType = type;
		return this;
	}
}
