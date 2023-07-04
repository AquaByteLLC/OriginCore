package blocks.impl.aspect.projection;

import blocks.BlocksAPI;
import blocks.block.aspects.AspectType;
import blocks.block.aspects.BlockAspect;
import blocks.block.aspects.projection.Projectable;
import blocks.block.builder.AspectHolder;
import blocks.block.illusions.FakeBlock;
import blocks.block.util.ClickCallback;
import blocks.impl.aspect.BaseAspect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

/**
 * @author vadim
 */
public class Projection extends BaseAspect implements Projectable {

	private BlockData fakeData;
	private ChatColor highlight;
	private ClickCallback callback;

	public Projection(AspectHolder editor) {
		super(editor, AspectType.PROJECTABLE);
	}

	@Override
	public BlockData getProjectedBlockData() {
		return fakeData;
	}

	@Override
	public void setProjectedBlockData(BlockData fakeData) {
		this.fakeData = fakeData;
	}

	@Override
	public ChatColor getHighlightColor() {
		return highlight;
	}

	@Override
	public void setHighlightColor(ChatColor color) {
		this.highlight = color;
	}

	@Override
	public ClickCallback getCallback() {
		return callback;
	}

	@Override
	public void setCallback(ClickCallback callback) {
		this.callback = callback;
	}

	@Override
	public FakeBlock toFakeBlock(Location location) {
		return BlocksAPI.getInstance().getIllusions().newIllusionBuilder()
				.fakeProjectedBlockData(fakeData)
				.overlayHighlightColor(highlight)
				.overlayClickCallback(callback)
				.build(location);
	}

	@Override
	public BlockAspect copy(AspectHolder newHolder) {
		Projection projection = new Projection(newHolder);
		projection.fakeData = fakeData.clone();
		return projection;
	}

}
