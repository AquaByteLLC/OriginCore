package generators.impl;

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
import org.bukkit.util.Vector;

/**
 * @author vadim
 */
public class GenHandler {

	private final ConfigurationProvider conf;
	private final GenRegistry reg;
	private final Task drops;

	public GenHandler(ConfigurationProvider conf, GenRegistry registry) {
		this.conf = conf;
		this.reg = registry;

		Config config = conf.open(Config.class);

		Events.merge(BlockEvent.class, BlockDamageEvent.class, BlockBreakEvent.class).handler(this::onCreateGen);
		Events.subscribe(BlockPlaceEvent.class).handler(this::onDestroyGen);

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

	void onCreateGen(BlockEvent event){
	}

	void onDestroyGen(BlockEvent event) {

	}

	public void shutdown() {
		drops.stop();
	}

}
