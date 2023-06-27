package blocks.impl.aspect.overlay;

import blocks.BlocksAPI;
import blocks.block.aspects.location.Locatable;
import blocks.block.aspects.overlay.Overlayable;
import blocks.block.aspects.overlay.registry.OverlayLocationRegistry;
import blocks.block.builder.OriginBlockBuilder;
import me.lucko.helper.text3.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class Overlay implements Overlayable {

	private final OriginBlockBuilder builder;
	private Location location;
	private TextColor color = TextColor.GREEN;
	private Consumer<Player> player;
	private final OverlayLocationRegistry locationRegistry;

	public Overlay(OriginBlockBuilder builder) {
		this.builder = builder;
		this.locationRegistry = BlocksAPI.getOverlayLocationRegistry();
	}

	@Override
	public OriginBlockBuilder getBuilder() {
		return this.builder;
	}



	@Override
	public Overlayable setOverlayColor(TextColor color) {
		this.color = color;
		return this;
	}

	@Override
	public TextColor getOverlayColor() {
		return this.color;
	}

	@Override
	public Consumer<Player> getPlayerConsumer() {
		return this.player;
	}

	@Override
	public Overlayable setPlayerConsumer(Consumer<Player> playerConsumer) {
		this.player = playerConsumer;
		return this;
	}

	@Override
	public OverlayLocationRegistry getRegistry() {
		return this.locationRegistry;
	}

	@Override
	public Location getBlockLocation() {
		return this.location;
	}

	@Override
	public Locatable setBlockLocation(Location location) {
		this.location = location;
		return this;
	}

	@Override
	public void registerLocation() {
		getRegistry().getOverlays().put(getBlockLocation(), this);
	}

	@Override
	public void unregisterLocation() {
		getRegistry().getOverlays().remove(getBlockLocation());
	}

}
