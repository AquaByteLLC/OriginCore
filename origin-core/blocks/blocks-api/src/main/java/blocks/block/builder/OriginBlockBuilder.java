package blocks.block.builder;

import blocks.block.aspects.GeneralAspect;
import blocks.block.factory.AspectFactory;

import java.util.Map;
import java.util.function.Consumer;

public interface OriginBlockBuilder {
	String getName();
	AspectFactory getFactory();
	OriginBlockBuilder setName(String name);
	int getModelData();
	OriginBlockBuilder setModelData(int modelData);
	OriginBlockBuilder createAspect(String name, GeneralAspect aspect);
	Map<String, GeneralAspect> getAspects();
	<T> OriginBlockBuilder handle(Class<T> eventClazz, Consumer<T> eventConsumer);
}
