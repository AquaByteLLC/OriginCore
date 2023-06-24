package blocks.factory;

import blocks.factory.interfaces.BlockFactory;
import blocks.factory.interfaces.OriginBlock;

public class OriginBlockFactory implements BlockFactory {

	@Override
	public OriginBlock newBlock() {
		return new OriginBlockImpl();
	}
}
