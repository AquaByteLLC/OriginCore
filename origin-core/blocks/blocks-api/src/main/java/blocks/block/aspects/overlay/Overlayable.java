package blocks.block.aspects.overlay;

import blocks.block.aspects.GeneralAspect;
import blocks.block.aspects.location.Locatable;
import blocks.block.aspects.overlay.registry.OverlayLocationRegistry;
import me.lucko.helper.text3.format.TextColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface Overlayable extends GeneralAspect, Locatable {
	Overlayable setOverlayColor(TextColor color);
	TextColor getOverlayColor();
	Consumer<Player> getPlayerConsumer();
	Overlayable setPlayerConsumer(Consumer<Player> playerConsumer);
	OverlayLocationRegistry getRegistry();
}
