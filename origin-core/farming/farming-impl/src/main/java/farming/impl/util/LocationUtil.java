package farming.impl.util;

import blocks.BlocksAPI;
import blocks.block.builder.FixedAspectHolder;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class LocationUtil {
	public static ArrayList<FixedAspectHolder> getBlocks(Block start, int radius){
		ArrayList<FixedAspectHolder> blocks = new ArrayList<>();
		for(double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++){
			for(double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++){
				for(double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++){
					Location loc = new Location(start.getWorld(), x, y, z);

					final FixedAspectHolder fah = BlocksAPI.getBlock(loc);
					if (fah != null) {
						if (BlocksAPI.inRegion(loc)) {
							blocks.add(fah);
						}
					}
				}
			}
		}
		return blocks;
	}

}
