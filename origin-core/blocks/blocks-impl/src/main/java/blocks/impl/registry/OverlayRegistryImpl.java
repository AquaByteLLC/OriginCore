package blocks.impl.registry;

import blocks.block.aspects.overlay.Overlayable;
import blocks.block.aspects.overlay.registry.OverlayLocationRegistry;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class OverlayRegistryImpl implements OverlayLocationRegistry {

	@Getter
	private final HashMap<Location, Overlayable> overlayMap;
	private final HashMap<Overlayable, UUID> falling;

	public OverlayRegistryImpl() {
		this.overlayMap = new HashMap<>();
		this.falling = new HashMap<>();
	}

	@Override
	public void createOverlay(Overlayable overlay, Location loc) {
		overlayMap.put(loc, overlay);
	}

	@Override
	public void deleteOverlay(Overlayable overlay, Location loc) {
		overlayMap.remove(loc);
	}

	@Override
	public @NotNull HashMap<Location, Overlayable> getOverlays() {
		return this.overlayMap;
	}

	@Override
	public @NotNull HashMap<Overlayable, UUID> getFalling() {
		return this.falling;
	}
}
