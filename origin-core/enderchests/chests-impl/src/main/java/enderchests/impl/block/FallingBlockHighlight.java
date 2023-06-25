package enderchests.impl.block;

import enderchests.block.BlockHighlight;
import org.bukkit.Location;

import java.awt.*;

/**
 * @author vadim
 */
public class FallingBlockHighlight extends BlockAdapter implements BlockHighlight {

	private final Color color;

	public FallingBlockHighlight(Location location, Color color) {
		super(location);
		this.color = color;
	}

	@Override
	public Color getHighlightColor() {
		return color;
	}

}
