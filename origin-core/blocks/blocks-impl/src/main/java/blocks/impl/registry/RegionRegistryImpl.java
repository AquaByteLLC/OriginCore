package blocks.impl.registry;

import blocks.block.regions.OriginRegion;
import blocks.block.regions.registry.RegionRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RegionRegistryImpl implements RegionRegistry {
	private final HashMap<String, OriginRegion.RegionInstance> regions;

	public RegionRegistryImpl() {
		this.regions = new HashMap<>();
	}

	@Override
	public void createRegion(String wgID, OriginRegion.RegionInstance instance) {
		regions.put(wgID, instance);
	}

	@Override
	public void deleteRegion(String wgID) {
		regions.remove(wgID);
	}

	@Override
	public @NotNull Map<String, OriginRegion.RegionInstance> getRegions() {
		return this.regions;
	}
}
