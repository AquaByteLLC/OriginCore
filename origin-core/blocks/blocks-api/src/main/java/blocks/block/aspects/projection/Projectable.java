package blocks.block.aspects.projection;

import blocks.block.aspects.BlockAspect;
import blocks.block.aspects.overlay.ClickCallback;
import blocks.block.illusions.FakeBlock;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.Locale;

/**
 * Represents a fake block data projected to the client, regardless of what the actual block is on the server.
 * Also supports block overlays.
 *
 * @author vadim
 */
public interface Projectable extends BlockAspect {

	BlockData getProjectedBlockData();

	void setProjectedBlockData(BlockData fakeData);

	ChatColor getHighlightColor();

	void setHighlightColor(ChatColor color);

	ClickCallback getCallback();

	void setCallback(ClickCallback callback);

	FakeBlock toFakeBlock(Location location);

}