package blocks.impl.illusions.impl;

import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionBuilder;
import blocks.block.util.ClickCallback;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;


/**
 * @author vadim
 */
class IllusionBuilderImpl implements IllusionBuilder {

	BlockData projected;

	boolean has;
	ChatColor highlight;
	BlockData overlay;
	ClickCallback click;

	@Override
	public IllusionBuilder fakeProjectedBlockData(BlockData fakeData) {
		this.projected = fakeData == null ? null : fakeData.clone();
		return this;
	}

	@Override
	public IllusionBuilder overlayHighlightColor(ChatColor color) {
		if(color != null) has = true;
		this.highlight = color;
		return this;
	}

	@Override
	public IllusionBuilder overlayData(BlockData overlayData) {
		if(overlayData != null) has = true;
		this.overlay = overlayData == null ? null : overlayData.clone();
		return this;
	}

	@Override
	public IllusionBuilder overlayClickCallback(ClickCallback callback) {
		if(callback != null) has = true;
		this.click = callback;
		return this;
	}

	@Override
	public FakeBlock build(Location location) {
		FallingBlockOverlay olay = null;
		if(has) {
			if (overlay == null)
				overlay = Material.GLASS.createBlockData();
			olay = new FallingBlockOverlay(location, highlight, overlay, click);
		}
		return new PacketBasedFakeBlock(location, projected, olay);
	}

}
