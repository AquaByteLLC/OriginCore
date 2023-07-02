package blocks.block.regions;

import blocks.BlocksAPI;
import blocks.block.regions.registry.RegionRegistry;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import lombok.Getter;
import org.bukkit.World;

public record OriginRegion(String regionID, String allowedBlock, World world) {

	public RegionInstance newInstance() {
		return new RegionInstance(this, allowedBlock);
	}

	@Getter
	public static final class RegionInstance {

		private final OriginRegion region;
		private final String block;
		@Getter
		private final ProtectedRegion wgRegion;
		private static final RegionRegistry registry = BlocksAPI.getInstance().getRegionRegistry();

		public RegionInstance(OriginRegion region, String block) {
			this.region = region;
			this.block = block;
			final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			System.out.println(region.regionID);
			System.out.println(region.regionID);
			System.out.println(region.regionID);
			System.out.println(region.regionID);
			System.out.println(region.regionID);
			System.out.println(region.regionID);
			System.out.println(region.regionID);

			this.wgRegion = container.get(BukkitAdapter.adapt(region.world)).getRegion(region.regionID());
			registry.createRegion(wgRegion.getId(), this);
		}

	}

}
