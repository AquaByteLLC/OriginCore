package blocks.impl.builder;

import blocks.block.aspects.GeneralAspect;
import blocks.block.builder.OriginBlockBuilder;
import blocks.block.factory.AspectFactory;
import blocks.impl.factory.AspectFactoryImpl;
import commons.events.impl.impl.PlayerEventSubscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OriginBlock implements OriginBlockBuilder {
	private String blockName;
	private int modelData;
	private Map<String, GeneralAspect> aspects;
	private final AspectFactory aspectFactory;
	private PlayerEventSubscriber<?> eventSubscriber;

	public OriginBlock() {
		this.aspects = new HashMap<>();
		this.aspectFactory = new AspectFactoryImpl(this);
	}

	@Override
	public String getName() {
		return this.blockName;
	}

	@Override
	public AspectFactory getFactory() {
		return this.aspectFactory;
	}

	@Override
	public OriginBlock setName(String name) {
		this.blockName = name;
		return this;
	}

	@Override
	public int getModelData() {
		return this.modelData;
	}

	@Override
	public OriginBlock setModelData(int modelData) {
		this.modelData = modelData;
		return this;
	}

	@Override
	public OriginBlockBuilder createAspect(String name, GeneralAspect aspect) {
		getAspects().put(name, aspect);
		return this;
	}

	@Override
	public Map<String, GeneralAspect> getAspects() {
		return this.aspects;
	}

	@Override
	public <T> OriginBlockBuilder handle(Class<T> eventClazz, Consumer<T> eventConsumer) {
		eventSubscriber = new PlayerEventSubscriber<>(eventClazz, ((context, event) -> eventConsumer.accept(event)));
		return this;
	}
}
