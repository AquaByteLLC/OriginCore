package farming.impl.events;

import blocks.BlocksAPI;
import blocks.block.aspects.AspectType;
import blocks.block.aspects.drop.Dropable;
import blocks.block.aspects.effect.Effectable;
import blocks.block.aspects.projection.Projectable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.anim.entity.BlockEntity;
import blocks.impl.builder.OriginBlock;
import blocks.impl.data.account.BlockAccount;
import blocks.impl.events.AbstractBreakEvent;
import blocks.impl.events.AbstractRegenEvent;
import commons.events.api.EventRegistry;
import commons.events.impl.impl.DetachedSubscriber;
import me.lucko.helper.Schedulers;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("FieldCanBeLocal")
public class FarmingEvents {

	private static DetachedSubscriber<AbstractRegenEvent> regenEvent;
	private static DetachedSubscriber<AbstractBreakEvent> breakEvent;
	private static DetachedSubscriber<BlockBreakEvent> blockBreakEvent;
	private static final BlocksPlugin plugin = BlocksPlugin.get().getInstance(BlocksPlugin.class);

	public static void init(EventRegistry registry) {
		initBreak();
		initRegen();
		initBlockBreak();

		breakEvent.bind(registry);
		regenEvent.bind(registry);
		blockBreakEvent.bind(registry);
	}

	private static void initBlockBreak() {
		blockBreakEvent = new DetachedSubscriber<>(BlockBreakEvent.class, (context, event) -> {
			final Block block = event.getBlock();
			final BlockData blockData = event.getBlock().getBlockData();

			if (blockData instanceof Ageable ageable) {
				if (BlocksAPI.inRegion(block.getLocation())) {
					if (ageable.getAge() == ageable.getMaximumAge()) {
						new AbstractBreakEvent(block, event.getPlayer()).callEvent();
						event.setCancelled(true);
					}
				}
			}
		});
	}

	private static void initBreak() {
		breakEvent = new DetachedSubscriber<>(AbstractBreakEvent.class, (context, event) -> {
			Player player = event.getPlayer();
			Block block = event.getBlock();

			final BlockAccount account = plugin.getAccounts().getAccount(player);
			if (account == null) return;

			final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();
			if (regenerationRegistry.getRegenerations().containsKey(CraftLocation.toBlockPosition(block.getLocation())))
				return;

			final List<ItemStack> clonedDrops = new ArrayList<>(block.getDrops());
			if (clonedDrops.isEmpty()) {
				return;
			}

			block.getDrops().clear();

			final World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
			final OriginBlock originBlock = (OriginBlock) BlocksAPI.getBlock(block.getLocation());

			if (originBlock == null) return;

			Effectable effectable = (Effectable) originBlock.getAspects().get(AspectType.EFFECTABLE);
			Dropable dropable = (Dropable) originBlock.getAspects().get(AspectType.DROPABLE);
			Projectable projectable = (Projectable) originBlock.getAspects().get(AspectType.PROJECTABLE);

			if (effectable != null) {
				effectable.getEffects().forEach(effect ->
						effect.getEffectType().handleEffect(player, block.getLocation()));
			}

			if (dropable != null) {
				dropable.getDrops().forEach(drop -> {
					// TODO: ITEM BACKPACKS?????
					new BlockEntity(player, nmsWorld, block, drop);
				});
			}

			FakeBlock fake = projectable.toFakeBlock(originBlock.getBlockLocation());
			Regenable regenable = (Regenable) originBlock.getAspects().get(AspectType.REGENABLE);
			if (regenable == null) return;
			if (regenable.getRegenTime() <= 0) return;

			regenable.setFakeBlock(fake);

			final long endTime = (long) (System.currentTimeMillis() + regenable.getRegenTime() * 1000);
			regenerationRegistry.createRegen(regenable, block);
			new AbstractRegenEvent(regenable, player, block, endTime).callEvent();
		});
	}

	private static void initRegen() {
		regenEvent = new DetachedSubscriber<>(AbstractRegenEvent.class, ((context, event) -> {

			final BlockAccount account = plugin.getAccounts().getAccount(event.getPlayer());
			if (account == null) return;

			final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();
			final Regenable regenBlock = event.getRegenable();
			final Player player = event.getPlayer();
			final Block block = event.getBlock();
			final IllusionRegistry illusionRegistry = event.getIllusionsAPI().localRegistry(player);
			final Location location = regenBlock.getFakeBlock().getBlockLocation();
			final Projectable projectable = (Projectable) regenBlock.getEditor().getAspects().get(AspectType.PROJECTABLE);
			final Ageable ageable = ((Ageable) block.getBlockData());

			Schedulers.bukkit().runTaskLater(plugin, $ -> {
				illusionRegistry.register(regenBlock.getFakeBlock());
				System.out.println(illusionRegistry.getBlockAt(regenBlock.getFakeBlock().getBlockLocation()));
			}, 3);

			AtomicInteger atomicAge = new AtomicInteger();
			Schedulers.bukkit().runTaskTimer(plugin, bukkitTask -> {
				if (System.currentTimeMillis() >= event.getEnd()) {

					Schedulers.bukkit().runTaskTimer(plugin, runnable -> {
						if (atomicAge.get() == ageable.getMaximumAge()) {
							regenerationRegistry.deleteRegen(block);
							illusionRegistry.unregister(regenBlock.getFakeBlock());
							BlocksAPI.resetBlock(block.getLocation());
							runnable.cancel();
						}

						ageable.setAge(atomicAge.getAndIncrement());
						projectable.setProjectedBlockData(ageable);

						illusionRegistry.register(projectable.toFakeBlock(location));
						}, 10, 10);

					bukkitTask.cancel();
				}
			}, 0, 5);
		}));
	}
}
