package generators.impl;

import commons.conf.wrapper.EffectGroup;
import commons.data.account.AccountProvider;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import generators.impl.conf.Config;
import generators.impl.conf.GensSettings;
import generators.impl.conf.Messages;
import generators.impl.conf.Tiers;
import generators.impl.data.GenAccount;
import generators.impl.wrapper.GenInfo;
import generators.impl.wrapper.PDCUtil;
import generators.wrapper.Generator;
import generators.wrapper.Tier;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.Location;
import org.bukkit.Sound;
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

	private final ConfigurationProvider conf;
	private final AccountProvider<GenAccount> provider;
	private final GenRegistry reg;
	private final Task drops;

	public GenHandler(ConfigurationProvider conf, EventRegistry events, GenRegistry registry, AccountProvider<GenAccount> provider) {
		this.conf     = conf;
		this.reg      = registry;
		this.provider = provider;

		Config config = conf.open(Config.class);

		events.subscribeAll(this);

		drops = Schedulers.sync().runRepeating(this::drop, config.getDropRateTicks(), config.getDropRateTicks());
	}

	private Config config() {
		return conf.open(Config.class);
	}

	private Messages msg() {
		return conf.open(Messages.class);
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
	void onCreateGen(BlockPlaceEvent event) {
		Player     player   = event.getPlayer();
		GenAccount account  = provider.getAccount(player);
		Location   location = event.getBlock().getLocation();

		if (PDCUtil.isDrop(event.getItemInHand())) {
			event.setCancelled(true);
			return;
		}

		if (!PDCUtil.isGen(event.getItemInHand()))
			return;

		Tier tier = conf.open(Tiers.class).findTier(event.getItemInHand().getType());
		if (tier == null) return;

		Generator generator = reg.getGenAt(location);
		if (generator != null) {
			event.setCancelled(true);
			msg().getInvalidLocation().sendTo(player, StringPlaceholder.EMPTY);
			return;
		}

		if (account.isAtSlotLimit() && !player.isOp()) {
			event.setCancelled(true);
			msg().getAtLimit().sendTo(player, StringPlaceholder.of("max_slots", String.valueOf(account.slotLimit)));
			return;
		}

		Generator gen = tier.toGenerator(player, location);
		reg.createGen(gen);
		config().getCreateEffect().sendToIf(player,
						location.clone(), GensSettings.SOUNDS::isEnabled,
						location.add(.5, 1.5, .5), GensSettings.PARTICLES::isEnabled);
		msg().getCreatedGen().sendTo(player, GenInfo.placeholdersForTier(gen.getCurrentTier()));
	}

	@Subscribe
	void onDeleteGen(BlockBreakEvent event) {
		Player   player   = event.getPlayer();
		Location location = event.getBlock().getLocation();

		Generator generator = reg.getGenAt(location);
		if (generator == null) return;

		event.setCancelled(true);

		EffectGroup effect =
				switch (generator.destroy(reg, player)) {
					case SUCCESS -> config().getDestroyEffect();
					case NO_PERMISSION -> config().getErrorEffect();
				};
		effect.sendToIf(player,
						location.clone(), GensSettings.SOUNDS::isEnabled,
						location.add(.5, 1.5, .5), GensSettings.PARTICLES::isEnabled);
		msg().getDestroyedGen().sendTo(player, GenInfo.placeholdersForTier(generator.getCurrentTier()));
	}

	@Subscribe
	void onUpgradeGen(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.isBlockInHand()) return;
		if (!event.getPlayer().isSneaking()) return;

		Player player = event.getPlayer();
		Block  block  = event.getClickedBlock();
		if (block == null) return;

		Location  location  = block.getLocation();
		Generator generator = reg.getGenAt(location);
		if (generator == null) return;

		if (!generator.isOwnedBy(player)) return; // do not allow upgrading other's gens

		EffectGroup effect =
				switch (generator.upgrade(reg)) {
					case SUCCESS -> config().getUpgradeEffect();
					case MAX_LEVEL -> EffectGroup.EMPTY;
					default -> config().getErrorEffect();
				};
		effect.sendToIf(player,
						location.clone(), GensSettings.SOUNDS::isEnabled,
						location.add(.5, 1.5, .5), GensSettings.PARTICLES::isEnabled);
		msg().getUpgradedGen().sendTo(player, GenInfo.placeholdersForTier(generator.getCurrentTier()));
	}

	@Subscribe
	@SuppressWarnings("DataFlowIssue")
	void onPickupItem(PlayerAttemptPickupItemEvent event) {
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
