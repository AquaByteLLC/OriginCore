package blocks.block.builder;

import blocks.block.effects.EffectType;

public interface EffectHolder {

	EffectType<?> getEffectType();

	void setEffectType(EffectType<?> type);

}
