package blocks.block.aspects.overlay.registry;

import blocks.block.aspects.overlay.Overlayable;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public interface OverlayLocationRegistry {
	void createOverlay(Overlayable overlayable, Location loc);
	void deleteOverlay(Overlayable overlayable, Location loc);
	@NotNull HashMap<Location, Overlayable> getOverlays();
	@NotNull HashMap<Overlayable, UUID> getFalling();
}
