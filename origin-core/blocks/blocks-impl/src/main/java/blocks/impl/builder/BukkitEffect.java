package blocks.impl.builder;

import blocks.block.builder.EffectHolder;
import blocks.block.effects.EffectType;

public class BukkitEffect implements EffectHolder {
	private EffectType<?> effectType;

	public BukkitEffect(EffectType<?> effectType) {
		this.effectType = effectType;
	}

	@Override
	public EffectType<?> getEffectType() {
		return this.effectType;
	}

	@Override
	public void setEffectType(EffectType<?> type) {
		this.effectType = type;
	}

}
