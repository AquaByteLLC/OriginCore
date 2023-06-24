package blocks.factory.effects;

import blocks.factory.interfaces.EffectType;
import blocks.factory.interfaces.OriginEffects;

public class BukkitEffect implements OriginEffects {

	private EffectType<?> effectType;

	public BukkitEffect(EffectType<?> effectType) {
		this.effectType = effectType;
	}

	@Override
	public EffectType<?> getEffectType() {
		return this.effectType;
	}

	@Override
	public OriginEffects setEffectType(EffectType<?> type) {
		this.effectType = type;
		return this;
	}
}
