package generators.impl;

import commons.data.AccountProvider;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import generators.impl.conf.Config;
import generators.impl.conf.Tiers;
import generators.impl.data.GenAccount;
import generators.impl.wrapper.PDCUtil;
import generators.wrapper.Generator;
import generators.wrapper.Tier;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * @author vadim
 */
public class GenHandler {

	private final ConfigurationProvider       conf;
	private final AccountProvider<GenAccount> provider;
	private final GenRegistry                 reg;
	private final Task                  drops;

	public GenHandler(ConfigurationProvider conf, EventRegistry events, GenRegistry registry, AccountProvider<GenAccount> provider) {
		this.conf = conf;
		this.reg  = registry;
		this.provider = provider;

		Config config = conf.open(Config.class);

		events.subscribeAll(this);

		drops = Schedulers.sync().runRepeating(this::drop, config.getDropRateTicks(), config.getDropRateTicks());
	}

	void drop() {
		for (Long2ObjectMap.Entry<Generator> entry : reg.all()) {
			Generator gen = entry.getValue();

			if (!gen.getOfflineOwner().isOnline())
				continue;

			ItemStack drop = PDCUtil.createDrop(gen);

			Location loc = gen.getBlockLocation();
			loc.add(.5, 1.5, .5);
			gen.getBlockLocation().getWorld().dropItem(loc, drop).setVelocity(new Vector(0, 0, 0));
		}
	}

	@Subscribe
	void onCreateGen(EventContext context, BlockPlaceEvent event) {
		Player player = context.getPlayer();
		GenAccount account = provider.getAccount(player);

		if(PDCUtil.isDrop(event.getItemInHand())) {
			event.setCancelled(true);
			return;
		}

		Tier tier = conf.open(Tiers.class).findTier(event.getItemInHand().getType());
		Generator generator = reg.getGenAt(event.getBlockPlaced().getLocation());

		if(generator != null) {
			event.setCancelled(true);
			player.sendMessage("akready a gen ther");
			return;
		}

		if(account.isAtSlotLimit() && !player.isOp()) {
			event.setCancelled(true);
			player.sendMessage("limit reached");
			return;
		}

		//create the gen

	}

	@Subscribe
	void onUnlinkGen(EventContext context, BlockBreakEvent event) {
		System.out.println(context.getPlayer().getName());
	}

	public void shutdown() {
		drops.stop();
	}

}
