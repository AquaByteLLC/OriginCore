package generators.impl;

import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import generators.impl.conf.Config;
import generators.impl.wrapper.Drops;
import generators.wrapper.Generator;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.util.Vector;

/**
 * @author vadim
 */
public class GenHandler {

	private final ConfigurationProvider conf;
	private final GenRegistry reg;
	private final Task drops;

	public GenHandler(ConfigurationProvider conf, EventRegistry events, GenRegistry registry) {
		this.conf = conf;
		this.reg = registry;

		Config config = conf.open(Config.class);

		events.subscribeAll(this);

		drops = Schedulers.sync().runRepeating(this::drop, config.getDropRateTicks(), config.getDropRateTicks());
	}

	void drop(){
		for (Long2ObjectMap.Entry<Generator> entry : reg.all()) {
			Generator gen = entry.getValue();

			if(!gen.getOfflineOwner().isOnline())
				continue;

			ItemStack drop = Drops.createDrop(gen);

			Location loc = gen.getBlockLocation();
			loc.add(.5, 1.5, .5);
			gen.getBlockLocation().getWorld().dropItem(loc, drop).setVelocity(new Vector(0, 0, 0));
		}
	}

	@Subscribe
	void onUnlinkGen(EventContext context, BlockBreakEvent event){
		System.out.println(context.getPlayer().getName());
	}

	@Subscribe
	void onCreateGen(EventContext context, BlockPlaceEvent event) {
		System.out.println(context.getPlayer().getName());
	}

	public void shutdown() {
		drops.stop();
	}

}
