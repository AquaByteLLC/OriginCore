package blocks.block.aspects;

import blocks.block.builder.AspectHolder;

@SuppressWarnings("DeprecatedIsStillUsed")
public interface BlockAspect {

	AspectType getAspectType();

	AspectHolder getEditor();

	/**
	 * @deprecated Not for API use.
	 */
	@Deprecated
	BlockAspect copy(AspectHolder newHolder);

}
