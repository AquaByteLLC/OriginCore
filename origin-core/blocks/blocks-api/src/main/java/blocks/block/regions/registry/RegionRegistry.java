package blocks.block.regions.registry;

import blocks.block.regions.OriginRegion;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface RegionRegistry {

	void createRegion(String wgID, OriginRegion.RegionInstance instance);

	void deleteRegion(String wgID);

	@NotNull HashMap<String, OriginRegion.RegionInstance> getRegions();

}
