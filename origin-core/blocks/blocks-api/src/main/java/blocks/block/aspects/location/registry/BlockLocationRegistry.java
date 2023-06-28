package blocks.block.aspects.location.registry;

import blocks.block.builder.AspectHolder;
import blocks.block.builder.FixedAspectHolder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockLocationRegistry {

	@Nullable FixedAspectHolder getBlockAt(Location location);

	@NotNull FixedAspectHolder createBlock(AspectHolder editor, Location block);

	void deleteBlock(FixedAspectHolder editor);

	void deleteBlock(Location location);

}
