package blocks.impl.factory;

import blocks.block.builder.OriginEffectBuilder;
import blocks.block.factory.EffectFactory;
import blocks.impl.builder.BukkitEffect;

public class EffectFactoryImpl implements EffectFactory {
	@Override
	public OriginEffectBuilder newEffect() {
		return new BukkitEffect(null);
	}
}
