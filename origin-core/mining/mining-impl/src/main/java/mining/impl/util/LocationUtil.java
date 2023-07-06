package mining.impl.util;

import blocks.BlocksAPI;
import blocks.block.builder.FixedAspectHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.List;

public class LocationUtil {
	public static List<FixedAspectHolder> getSphereHolders(Player player, Location location, int sphereRadius) {
		final List<FixedAspectHolder> holders = new ArrayList<>();

		final double centerX = location.getBlockX() + 0.5;
		final double centerY = location.getBlockY() + 0.5;
		final double centerZ = location.getBlockZ() + 0.5;
		final double radiusSquared = NumberConversions.square(sphereRadius);
		final World world = location.getWorld();

		for (double x = centerX - sphereRadius; x <= centerX + sphereRadius; x++) {
			for (double y = centerY - sphereRadius; y <= centerY + sphereRadius; y++) {
				for (double z = centerZ - sphereRadius; z <= centerZ - sphereRadius; z++) {

					final double distanceSquared = NumberConversions.square(x - centerX) + NumberConversions.square(y - centerY) + NumberConversions.square(z - centerZ);

					if (distanceSquared < radiusSquared) {
						final Block block = world.getBlockAt((int) x, (int) y, (int) z);
						final FixedAspectHolder fah = BlocksAPI.getBlock(block.getLocation());
						if (fah != null) {
							if (BlocksAPI.inRegion(block.getLocation())) {
								holders.add(fah);
							}
						}
					}
				}
			}
		}
		return holders;
	}

}
