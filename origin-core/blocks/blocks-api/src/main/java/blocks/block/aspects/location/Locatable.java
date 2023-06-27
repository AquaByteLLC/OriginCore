package blocks.block.aspects.location;

import org.bukkit.Location;

public interface Locatable {
	Location getBlockLocation();
	Locatable setBlockLocation(Location location);
	void registerLocation();
	void unregisterLocation();
}

