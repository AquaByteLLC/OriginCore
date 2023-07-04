package blocks.impl.aspect.overlay;

import blocks.block.aspects.AspectType;
import blocks.block.aspects.BlockAspect;
import blocks.block.aspects.overlay.Overlayable;
import blocks.block.builder.AspectHolder;
import blocks.block.util.ClickCallback;
import blocks.impl.aspect.BaseAspect;
import org.bukkit.ChatColor;
import org.bukkit.block.data.BlockData;

public class Overlay extends BaseAspect implements Overlayable {

	private BlockData overlayData;
	private ChatColor color = ChatColor.GREEN;
	private ClickCallback callback;

	public Overlay(AspectHolder editor) {
		super(editor, AspectType.OVERLAYABLE);
	}

	@Override
	public void setHighlightColor(ChatColor color) {
		this.color = color;
	}

	@Override
	public ChatColor getHighlightColor() {
		return this.color;
	}

	@Override
	public void setOverlayData(BlockData overlayData) {
		this.overlayData = overlayData;
	}

	@Override
	public BlockData getOverlayData() {
		return overlayData;
	}

//	@Override
//	public BlockOverlay toOverlay() {
//		return new FallingBlockOverlay();
//	}

	@Override
	public ClickCallback getClickCallback() {
		return this.callback;
	}

	@Override
	public void setClickCallback(ClickCallback callback) {
		this.callback = callback;
	}

	@Override
	public BlockAspect copy(AspectHolder newHolder) {
		Overlay overlay = new Overlay(newHolder);
		overlay.overlayData = overlayData.clone();
		overlay.callback = callback;
		overlay.color = color;
		return overlay;
	}

}
