package blocks.block.aspects.effect;

import blocks.block.aspects.BlockAspect;
import blocks.block.builder.EffectHolder;

import java.util.List;

public interface Effectable extends BlockAspect {

	List<EffectHolder> getEffects();

	Effectable addEffect(EffectHolder effectFactory);

	Effectable removeEffect(EffectHolder effectFactory);

}
