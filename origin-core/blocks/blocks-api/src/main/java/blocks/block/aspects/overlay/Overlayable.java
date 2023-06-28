package blocks.block.aspects.overlay;

import blocks.block.aspects.BlockAspect;
import blocks.block.illusions.BlockOverlay;
import org.bukkit.ChatColor;
import org.bukkit.block.data.BlockData;

/**
 * Represents a {@link org.bukkit.entity.FallingBlock falling block} overlay onto a block, with optional {@link org.bukkit.entity.Entity#setGlowing(boolean) glow} highlighting.
 */
public interface Overlayable extends BlockAspect {

	void setHighlightColor(ChatColor color);

	ChatColor getHighlightColor();

	void setOverlayData(BlockData overlayData);

	BlockData getOverlayData();

	ClickCallback getClickCallback();

	void setClickCallback(ClickCallback callback);

//	BlockOverlay toOverlay();

}
