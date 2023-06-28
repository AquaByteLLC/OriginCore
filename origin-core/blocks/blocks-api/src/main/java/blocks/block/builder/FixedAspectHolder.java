package blocks.block.builder;

import blocks.block.aspects.BlockAspect;
import blocks.block.aspects.location.BlockLike;

public interface FixedAspectHolder extends BlockLike, AspectHolder {

	@Override
	FixedAspectHolder setName(String name);

	@Override
	FixedAspectHolder setModelData(int modelData);

	@Override
	FixedAspectHolder createAspect(BlockAspect aspect);

}
