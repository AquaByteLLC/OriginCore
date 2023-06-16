package commons;

import org.bukkit.Location;

/**
 * @author vadim
 */
public class Util {

	public static long packLoc(int x, int y, int z) {
		return ((long)x & 67108863L) << 38 | (long)y & 4095L | ((long)z & 67108863L) << 12;
	}

	public static long packLoc(Location location){
		return packLoc(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public static Location unpackLoc(long location){
		return new Location(null, (int)(location >> 38), (int)(location << 52 >> 52), (int)(location << 26 >> 38));
	}

}
