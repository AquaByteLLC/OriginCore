package blocks.block.illusions;

import blocks.block.aspects.location.BlockLike;
import blocks.block.util.ClickCallback;
import org.bukkit.ChatColor;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public interface BlockOverlay extends BlockLike, Illusion {

	@Nullable ChatColor getHighlightColor();

	boolean isHighlighted();

	BlockData getOverlayData();

	boolean hasOverlayData();

	ClickCallback getCallback();

}
