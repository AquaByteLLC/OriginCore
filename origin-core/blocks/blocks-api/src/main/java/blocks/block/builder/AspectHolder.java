package blocks.block.builder;

import blocks.block.aspects.AspectType;
import blocks.block.aspects.BlockAspect;
import blocks.block.factory.AspectFactory;
import org.bukkit.Location;

import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public interface AspectHolder {

	String getName();

	AspectFactory getFactory();

	AspectHolder setName(String name);

	int getModelData();

	AspectHolder setModelData(int modelData);

	AspectHolder createAspect(BlockAspect aspect);

	Map<AspectType, BlockAspect> getAspects();

	<T> AspectHolder handle(Class<T> eventClazz, Consumer<T> eventConsumer);

	FixedAspectHolder asLocationBased(Location location);

}