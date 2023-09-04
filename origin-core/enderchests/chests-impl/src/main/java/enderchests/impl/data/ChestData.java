package enderchests.impl.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import commons.util.num.PackUtil;
import enderchests.NetworkColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.UUID;

/**
 * ORMLite version of {@link enderchests.impl.LinkedEnderChest}. I though this might be cleaner than cluttering up the impl class.
 * @author vadim
 */
@DatabaseTable
class ChestData {

	@DatabaseField(columnName = "owner_uuid")
	UUID owner;

	@DatabaseField
	BlockFace face;

	@DatabaseField
	NetworkColor network;

	@DatabaseField
	long location;

	@DatabaseField
	UUID world;

	Location getLocation() {
		Location loc = PackUtil.unpackLoc(location);
		loc.setWorld(Bukkit.getWorld(world));
		return loc;
	}

	void setLocation(Location loc) {
		world = loc.getWorld().getUID();
		location = PackUtil.packLoc(loc);
	}

}
