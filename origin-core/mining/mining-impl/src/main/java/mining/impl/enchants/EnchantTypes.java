package mining.impl.enchants;

import blocks.BlocksAPI;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.builder.FixedAspectHolder;
import blocks.block.progress.registry.ProgressRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.account.BlockAccount;
import commons.events.api.EventContext;
import commons.events.impl.EventSubscriber;
import commons.events.impl.impl.DetachedSubscriber;
import commons.util.reflect.FieldAccess;
import commons.util.reflect.Reflection;
import enchants.EnchantKey;
import enchants.EnchantRegistry;
import enchants.impl.item.EnchantedItemImpl;
import enchants.item.EnchantFactory;
import enchants.item.EnchantTarget;
import enchants.item.EnchantedItem;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static mining.impl.util.LocationUtil.fracture;

public enum EnchantTypes implements EnchantKey {

	FRACTURE("Fracture",
			subscribe(PacketPlayInBlockDig.class, (key, ctx, event) -> {
				final ItemStack playersItem = ctx.getPlayer().getInventory().getItemInMainHand();
				final BlocksPlugin plugin = BlocksPlugin.get().getInstance(BlocksPlugin.class);
				final FieldAccess<PacketPlayInBlockDig.EnumPlayerDigType> c = Reflection.unreflectFieldAccess(PacketPlayInBlockDig.class, "c");
				final FieldAccess<BlockPosition> a = Reflection.unreflectFieldAccess(PacketPlayInBlockDig.class, "a");

				if (playersItem.getType().isAir()) return;

				final EnchantedItem item = new EnchantedItemImpl(playersItem);
				final Player player = ctx.getPlayer();
				final Block block = player.getTargetBlockExact(5);

				if (block == null) return;

				if (BlocksAPI.inRegion(block.getLocation())) {
					final BlockAccount account = plugin.getAccounts().getAccount(player);
					final ProgressRegistry progressRegistry = account.getProgressRegistry();
					final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();
					final BlockPosition blockPos = a.get(event);

					if (item.activate(key)) {
						List<FixedAspectHolder> aspectList = fracture(player, block, blockPos);
						aspectList.forEach(holder -> {
							final Block holderBlock = holder.getBlock();
							final Location holderLoc = holderBlock.getLocation();
							final BlockPosition position = new BlockPosition(holderLoc.getBlockX(), holderLoc.getBlockY(), holderLoc.getBlockZ());

							if (BlocksAPI.inRegion(holderLoc)) {
								double current = progressRegistry.getBlockProgress().get(position) != null ? progressRegistry.getBlockProgress().get(position) : 0.0;
								double newProg = current + 20;
								progressRegistry.getBlocksBreaking().put(position, true);
								progressRegistry.getBlockProgress().put(position, newProg);
							}
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
