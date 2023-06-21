package generators.impl.wrapper;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import commons.impl.PlayerOwned;
import generators.wrapper.Generator;
import generators.wrapper.Tier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.UUID;

/**
 * @author vadim
 */
@DatabaseTable
public class Gen extends PlayerOwned implements Generator {

	private final @DatabaseField Tier     tier;
	private final @DatabaseField Location location;
	private final @DatabaseField UUID     world; // ORMLite workaround

	private Gen() { // ORMLite
		super(null);
		this.tier = null;
		this.location = null;
		this.world = null;
	}

	public Gen(OfflinePlayer owner, Tier tier, Location location) {
		super(owner.getUniqueId());
		this.tier     = tier;
		this.location = location.getBlock().getLocation().clone();
		this.world    = location.getWorld().getUID();
	}

	@Override
	public Tier getCurrentTier() {
		return tier;
	}

	@Override
	public Location getBlockLocation() {
		if(location.getWorld() == null)
			location.setWorld(Bukkit.getWorld(world));
		return location.clone();
	}

}
