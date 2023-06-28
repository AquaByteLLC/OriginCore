package blocks.impl.factory;

import blocks.block.builder.EffectHolder;
import blocks.block.factory.EffectFactory;
import blocks.impl.builder.BukkitEffect;

public class EffectFactoryImpl implements EffectFactory {
	@Override
	public EffectHolder newEffect() {
		return new BukkitEffect(null);
	}
}
