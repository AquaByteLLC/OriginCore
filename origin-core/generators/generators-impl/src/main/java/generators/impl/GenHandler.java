package generators.impl;

import commons.data.AccountProvider;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import generators.impl.conf.Config;
import generators.impl.conf.Tiers;
import generators.impl.data.GenAccount;
import generators.impl.wrapper.Gen;
import generators.impl.wrapper.PDCUtil;
import generators.wrapper.Generator;
import generators.wrapper.Tier;
import generators.wrapper.Upgrade;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * @author vadim
 */
public class GenHandler {

	private final ConfigurationProvider       conf;
	private final AccountProvider<GenAccount> provider;
	private final GenRegistry                 reg;
	private final Task                        drops;

	public GenHandler(ConfigurationProvider conf, EventRegistry events, GenRegistry registry, AccountProvider<GenAccount> provider) {
		this.conf     = conf;
		this.reg      = registry;
		this.provider = provider;

		Config config = conf.open(Config.class);

		events.subscribeAll(this);

		drops = Schedulers.sync().runRepeating(this::drop, config.getDropRateTicks(), config.getDropRateTicks());
	}

	void drop() {
		for (Long2ObjectMap.Entry<Generator> entry : reg.iterable()) {
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
		Player     player   = context.getPlayer();
		GenAccount account  = provider.getAccount(player);
		Location   location = event.getBlock().getLocation();

		if (PDCUtil.isDrop(event.getItemInHand())) {
			event.setCancelled(true);
			return;
		}

		Tier tier = conf.open(Tiers.class).findTier(event.getItemInHand().getType());
		if (tier == null) return;

		Generator generator = reg.getGenAt(location);
		if (generator != null) {
			event.setCancelled(true);
			player.sendMessage("akready a gen ther");
			return;
		}

		if (account.isAtSlotLimit() && !player.isOp()) {
			event.setCancelled(true);
			player.sendMessage("limit reached");
			return;
		}

		Generator gen = new Gen(player, tier, location);
		reg.createGen(gen);
		player.sendMessage("made a new gen ;P");
	}

	@Subscribe
	void onDeleteGen(EventContext context, BlockBreakEvent event) {
		Player     player   = context.getPlayer();
		GenAccount account  = provider.getAccount(player);
		Location   location = event.getBlock().getLocation();

		Generator generator = reg.getGenAt(location);
		if (generator == null) return;

		boolean isOwner = generator.getOwnerUUID().equals(player.getUniqueId());
		if (!isOwner && !player.isOp()) {
			player.sendMessage("dont break other ppl's gens >:[");
			event.setCancelled(true);
			return;
		}

		reg.deleteGen(generator);
		player.sendMessage("broke " + (isOwner ? "ur" : generator.getOfflineOwner().getName() + "'s") + " gen :o");
	}

	@Subscribe
	void onUpgradeGen(EventContext context, PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if(event.isBlockInHand()) return;
		if(!event.getPlayer().isSneaking()) return;

		Player     player  = context.getPlayer();
		GenAccount account = provider.getAccount(player);
		Block      block   = event.getClickedBlock();
		if (block == null) return;

		Location  location  = block.getLocation();
		Generator generator = reg.getGenAt(location);
		if (generator == null) return;

		Upgrade upgrade = generator.getCurrentTier().getNextUpgrade();
		if (upgrade == null) return; // max lvl

		Tier tier = upgrade.getNextTier();

		//todo: econ
		//todo: sound
		Generator upgraded = new Gen(player, tier, location);
		reg.createGen(upgraded);
		block.setType(tier.getBlock());
		player.sendMessage("upgraded <3");
	}

	@Subscribe
	@SuppressWarnings("DataFlowIssue")
	void onPickupItem(EventContext context, PlayerAttemptPickupItemEvent event) {
		UUID      player = event.getPlayer().getUniqueId();
		ItemStack item   = event.getItem().getItemStack();

		if (PDCUtil.isDrop(item))
			if (!player.equals(PDCUtil.getDropOwner(item).getUniqueId()))
				event.setCancelled(true); // prevent picking up other's drops

		if (PDCUtil.isGen(item))
			if (!player.equals(PDCUtil.getGenOwner(item).getUniqueId()))
				event.setCancelled(true); // prevent picking up other's gens
	}

	public void shutdown() {
		drops.stop();
	}

}
