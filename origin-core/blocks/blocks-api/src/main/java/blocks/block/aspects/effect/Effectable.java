package blocks.block.aspects.effect;

import blocks.block.aspects.GeneralAspect;
import blocks.block.builder.OriginEffectBuilder;

import java.util.List;

public interface Effectable extends GeneralAspect {
	List<OriginEffectBuilder> getEffects();
	Effectable addEffect(OriginEffectBuilder effectFactory);
	Effectable removeEffect(OriginEffectBuilder effectFactory);
}
