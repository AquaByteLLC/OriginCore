package blocks.impl.illusions.impl;

import blocks.block.illusions.IllusionBuilder;
import blocks.block.illusions.IllusionFactory;
import blocks.block.illusions.IllusionRegistry;
import blocks.block.illusions.IllusionsAPI;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author vadim
 */
public class Illusions implements IllusionsAPI {

	private final JavaPlugin plugin;
	private final EventRegistry events;
	@Deprecated
	private final IllusionFactory $;
	private final IllusionRegistry global;
	private final Map<UUID, IllusionRegistry> local = new HashMap<>();

	public Illusions(JavaPlugin plugin, EventRegistry events) {
		this.plugin = plugin;
		this.events = events;
		this.$      = new IllusionFactoryImpl();
		this.global = new BlockIllusionRegistry(plugin, new ServerPacketReceiver(), events);
		events.subscribeAll(this);
	}

	@Override
	public IllusionFactory factory() {
		return $;
	}

	@Override
	@Deprecated
	public IllusionRegistry registry() {
		return global;
	}

	@Override
	public IllusionRegistry globalRegistry() {
		return global;
	}

	@Override
	public IllusionRegistry localRegistry(Player player) {
		return local.computeIfAbsent(player.getUniqueId(), x -> new BlockIllusionRegistry(plugin, new PlayerPacketReceiver(player), events));
	}

	@Override
	public IllusionBuilder newIllusionBuilder() {
		return new IllusionBuilderImpl();
	}

	// performance
	@Subscribe
	void leave(PlayerQuitEvent event) {
		IllusionRegistry reg = local.get(event.getPlayer().getUniqueId());
		if(reg != null)
			events.unsubscribe(reg);
	}

}
