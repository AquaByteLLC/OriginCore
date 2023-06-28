package blocks.impl.aspect.effect;

import blocks.block.aspects.AspectType;
import blocks.block.aspects.BlockAspect;
import blocks.block.aspects.effect.Effectable;
import blocks.block.builder.AspectHolder;
import blocks.block.builder.EffectHolder;
import blocks.impl.aspect.BaseAspect;

import java.util.ArrayList;
import java.util.List;

public class Effect extends BaseAspect implements Effectable {
	private final List<EffectHolder> effectBuilders;

	public Effect(AspectHolder editor) {
		super(editor, AspectType.EFFECTABLE);
		this.effectBuilders = new ArrayList<>();
	}

	@Override
	public List<EffectHolder> getEffects() {
		return effectBuilders;
	}

	@Override
	public Effectable addEffect(EffectHolder effectFactory) {
		effectBuilders.add(effectFactory);
		return this;
	}

	@Override
	public Effectable removeEffect(EffectHolder effectFactory) {
		effectBuilders.remove(effectFactory);
		return this;
	}

	@Override
	public BlockAspect copy(AspectHolder newHolder) {
		Effect effect = new Effect(newHolder);
		effect.effectBuilders.addAll(effectBuilders);
		return effect;
	}

}
