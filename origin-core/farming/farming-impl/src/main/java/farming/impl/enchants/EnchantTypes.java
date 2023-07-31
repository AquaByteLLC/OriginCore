package farming.impl.enchants;

import blocks.BlocksAPI;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.builder.FixedAspectHolder;
import blocks.impl.BlocksPlugin;
import blocks.impl.data.account.BlockAccount;
import blocks.impl.events.BreakEvent;
import commons.events.api.EventContext;
import commons.events.impl.EventSubscriber;
import commons.events.impl.impl.DetachedSubscriber;
import enchants.EnchantKey;
import enchants.EnchantRegistry;
import enchants.impl.item.EnchantedItemImpl;
import enchants.item.EnchantFactory;
import enchants.item.EnchantTarget;
import enchants.item.EnchantedItem;
import farming.impl.enchants.entity.ExplosiveEntity;
import farming.impl.util.LocationUtil;
import me.lucko.helper.Schedulers;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

;

public enum EnchantTypes implements EnchantKey {

	EXPLOSION("Explosion",
			subscribe(BreakEvent.class, (key, ctx, event) -> {
				if (event.isCalledFromEnchant()) return;

				final ItemStack playersItem = ctx.getPlayer().getInventory().getItemInMainHand();
				final BlocksPlugin plugin = BlocksPlugin.get().getInstance(BlocksPlugin.class);

				if (playersItem.getType().isAir()) return;

				final EnchantedItem item = new EnchantedItemImpl(playersItem);
				final Player player = ctx.getPlayer();
				final Block block = player.getTargetBlockExact(5);

				if (block == null) return;

				if (BlocksAPI.inRegion(block.getLocation())) {
					final BlockAccount account = plugin.getAccounts().getAccount(player);
					final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();

					if (item.activate(key)) {
						new ExplosiveEntity(((CraftWorld) player.getWorld()).getHandle(), player, block, entity -> {
							Schedulers.bukkit().runTask(plugin, () -> {

								List<FixedAspectHolder> aspectList = LocationUtil.getBlocks(block, 100);
								aspectList.forEach(holder -> {
									if (holder.getBlock().getBlockData() instanceof Ageable ageable) {
										if (BlocksAPI.inRegion(block.getLocation())) {
											if (ageable.getAge() == ageable.getMaximumAge()) {
												new BreakEvent("farming", holder.getBlock(), player, true).callEvent();
											}
										}
									}
								});
							});
						});
					}
				}
			}), EnchantTarget.tools());
	private final String name;
	private final NamespacedKey key;
	private final EventSubscriber subscriber;
	private final EnchantTarget[] targets;

	EnchantTypes(String name, EventSubscriber subscriber, EnchantTarget... targets) {
		this.name = name;
		this.key = name2key(name);
		this.subscriber = subscriber;
		this.targets = targets;
	}

	@Override
	public NamespacedKey getNamespacedKey() {
		return key;
	}

	@Override
	public String getName() {
		return name;
	}

	public static NamespacedKey name2key(String name) {
		return new NamespacedKey("enchants", "enchant." + ChatColor.stripColor(name.toLowerCase()).replace(' ', '_'));
	}

	/* we perform miniscule amount of static abuse */

	private static int v = 0;

	private static <T> EventSubscriber subscribe(Class<T> clazz, Consumer3<T> cons) {
		final int vf = v++;
		return new DetachedSubscriber<>(clazz, (ctx, event) -> cons.consume(values()[vf], ctx, event));
	}


	@FunctionalInterface
	private interface Consumer3<T> {
		void consume(EnchantKey key, EventContext context, T event);
	}

	public static @Nullable EnchantKey fromName(String name) {
		for (EnchantTypes value : values())
			if (value.name.equalsIgnoreCase(name))
				return value;
		return null;
	}

	private static boolean init = false;

	public static void init(EnchantRegistry registry, EnchantFactory factory) {
		if (init)
			throw new UnsupportedOperationException();
		init = true;
		for (EnchantTypes value : values())
			registry.register(factory.newEnchantBuilder(value).build(value.subscriber, value.targets));
	}

}
