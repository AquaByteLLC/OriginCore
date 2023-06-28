package blocks.block.illusions;

import blocks.block.aspects.location.BlockLike;
import blocks.block.util.ClickCallback;
import org.bukkit.ChatColor;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.jetbrains.annotations.Nullable;

/**
 * @author vadim
 */
@SuppressWarnings("DeprecatedIsStillUsed")
public interface BlockOverlay extends BlockLike {

	@Nullable ChatColor getHighlightColor();

	boolean isHighlighted();

	BlockData getOverlayData();

	ClickCallback getCallback();

	/**
	 * @deprecated Not for API use.
	 */
	@Deprecated
	FallingBlock spawnNew();

}
