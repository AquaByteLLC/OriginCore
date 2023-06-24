package blocks.region;

import blocks.BlocksAPI;
import blocks.registry.BlockRegistry;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public record OriginRegion(World world, String regionID, String allowedBlock) {

	public RegionInstance newInstance() {
		return new RegionInstance(this, allowedBlock);
	}

	static final ConcurrentHashMap<String, RegionInstance> regions = BlocksAPI.getBlockRegistry().getRegions();

	public static RegionInstance getInstance(Location location) {
		for (IWrappedRegion region : WorldGuardWrapper.getInstance().getRegions(location)) {
			if (regions.containsKey(region.getId())) {
				return regions.get(region.getId());
			}
		}
		return null;
	}

	@Getter
	public static final class RegionInstance {
		private final OriginRegion region;
		private final String block;
		private final Optional<IWrappedRegion> wgRegion;
		private static final BlockRegistry registry = BlocksAPI.getBlockRegistry();

		public RegionInstance(OriginRegion region, String block) {
			this.region = region;
			this.block = block;
			final WorldGuardWrapper wrapper = WorldGuardWrapper.getInstance();
			this.wgRegion = wrapper.getRegion(region.world(), region.regionID());
			registry.getRegions().put(region.regionID(), this);
		}
	}
}
