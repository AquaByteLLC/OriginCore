package blocks.block.illusions;

import blocks.block.util.ClickCallback;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public interface IllusionBuilder {

	IllusionBuilder fakeProjectedBlockData(BlockData fakeData);

	IllusionBuilder overlayHighlightColor(ChatColor color);

	IllusionBuilder overlayData(BlockData overlayData);

	IllusionBuilder overlayClickCallback(ClickCallback callback);

	FakeBlock build(Location location);

}
