package blocks.impl.illusions;

import blocks.block.illusions.IllusionFactory;
import blocks.block.illusions.IllusionRegistry;
import blocks.block.illusions.IllusionsAPI;

/**
 * @author vadim
 */
public class Illusions implements IllusionsAPI {

	private final IllusionFactory factory;
	private final IllusionRegistry registry;

	public Illusions(IllusionFactory factory, IllusionRegistry registry) {
		this.factory  = factory;
		this.registry = registry;
	}

	@Override
	public IllusionFactory factory() {
		return factory;
	}

	@Override
	public IllusionRegistry registry() {
		return registry;
	}

}
