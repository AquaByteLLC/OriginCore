package blocks.impl.builder;

import blocks.block.aspects.AspectType;
import blocks.block.aspects.BlockAspect;
import blocks.block.builder.AspectHolder;
import blocks.block.builder.FixedAspectHolder;
import blocks.block.factory.AspectFactory;
import blocks.impl.factory.AspectFactoryImpl;
import blocks.impl.illusions.BlockAdapter;
import commons.events.impl.EventSubscriber;
import commons.events.impl.impl.GenericEventSubscriber;
import commons.events.impl.impl.PlayerEventSubscriber;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class OriginBlock extends BlockAdapter implements FixedAspectHolder {
	private String blockName;
	private int modelData;
	private final Map<AspectType, BlockAspect> aspects = new HashMap<>();
	private final AspectFactory aspectFactory;
	private final List<EventSubscriber> eventSubscribers = new ArrayList<>();

	public OriginBlock() {
		this(null);
	}

	private OriginBlock(Location location) {
		super(location);
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
	public FixedAspectHolder createAspect(BlockAspect aspect) {
		getAspects().put(aspect.getAspectType(), aspect);
		return this;
	}

	@Override
	public Map<AspectType, BlockAspect> getAspects() {
		return this.aspects;
	}

	@Override
	public <T> FixedAspectHolder handle(Class<T> eventClazz, Consumer<T> eventConsumer) {
		if(true) throw new UnsupportedOperationException("this operation is not implemented. none of the eventSubscribers are ever binded");
		eventSubscribers.add(new PlayerEventSubscriber<>(eventClazz, ((context, event) -> eventConsumer.accept(event))));
		return this;
	}

	@Override
	public FixedAspectHolder asLocationBased(Location location) {
		OriginBlock copy = new OriginBlock(location.getBlock().getLocation());

		copy.modelData = modelData;
		copy.eventSubscribers.addAll(eventSubscribers);
		copy.blockName = blockName;

		for (Map.Entry<AspectType, BlockAspect> entry : aspects.entrySet())
			copy.aspects.put(entry.getKey(), entry.getValue().copy(copy));

		return copy;
	}

}
