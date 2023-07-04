package blocks.impl.illusions.impl;

import blocks.block.illusions.IllusionBuilder;
import blocks.block.illusions.IllusionFactory;
import blocks.block.illusions.IllusionRegistry;
import blocks.block.illusions.IllusionsAPI;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
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
		return local.computeIfAbsent(player.getUniqueId(), uuid -> new BlockIllusionRegistry(plugin, new PlayerPacketReceiver(uuid), events));
	}

	@Override
	public IllusionBuilder newIllusionBuilder() {
		return new IllusionBuilderImpl();
	}

	// unsubscribe on quit, to prevent invoking methods that won't even run past the first line
	@Subscribe
	void quit(PlayerQuitEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			IllusionRegistry reg = local.get(event.getPlayer().getUniqueId());
			if(reg != null)
				events.unsubscribe(reg);
		});
	}

	// resubscribe on join, but do so async to avoid lag caused by reflective calls in subscribeAll
	@Subscribe
	void join(AsyncPlayerPreLoginEvent event) {
		IllusionRegistry reg = local.get(event.getUniqueId());
		if(reg != null)
			events.subscribeAll(reg);
	}

}
