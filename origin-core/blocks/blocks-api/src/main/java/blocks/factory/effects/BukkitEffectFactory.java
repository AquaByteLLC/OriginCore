package blocks.factory.effects;

import blocks.factory.interfaces.EffectFactory;
import blocks.factory.interfaces.OriginEffects;

public class BukkitEffectFactory implements EffectFactory {
	@Override
	public OriginEffects newEffect() {
		return new BukkitEffect(null);
	}
}
