package blocks.impl.aspect;

import blocks.block.aspects.AspectType;
import blocks.block.aspects.BlockAspect;
import blocks.block.builder.AspectHolder;

/**
 * @author vadim
 */
public abstract class BaseAspect implements BlockAspect {

	private final AspectHolder editor;
	private final AspectType type;

	public BaseAspect(AspectHolder editor, AspectType type) {
		this.editor = editor;
		this.type = type;
	}

	@Override
	public AspectType getAspectType() {
		return type;
	}

	@Override
	public AspectHolder getEditor() {
		return editor;
	}

}
