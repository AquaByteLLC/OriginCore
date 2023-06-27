package blocks.impl.factory;

import blocks.block.factory.BlockFactory;
import blocks.impl.builder.OriginBlock;

public class BlockFactoryImpl implements BlockFactory {
	@Override
	public OriginBlock newBlock() {
		return new OriginBlock();
	}
}
