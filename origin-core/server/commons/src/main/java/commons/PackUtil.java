package commons;

import org.bukkit.Location;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @author vadim
 */
public class PackUtil {

	public static final Location ZERO = new Location(null, 0, 0, 0);

	public static long packLoc(int x, int y, int z) {
		return ((long)x & 67108863L) << 38 | (long)y & 4095L | ((long)z & 67108863L) << 12;
	}

	public static long packLoc(Location location){
		return packLoc(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public static Location unpackLoc(long location){
		return new Location(null, (int)(location >> 38), (int)(location << 52 >> 52), (int)(location << 26 >> 38));
	}

	public static byte[] uuid2bytes(UUID uid){
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uid.getMostSignificantBits());
		bb.putLong(uid.getLeastSignificantBits());
		return bb.array();
	}

	public static UUID bytes2uuid(byte[] buf){
		ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
		return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
	}

}
