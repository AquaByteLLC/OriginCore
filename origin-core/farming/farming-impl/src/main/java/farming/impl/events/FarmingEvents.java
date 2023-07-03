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
import blocks.block.progress.registry.ProgressRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.anim.entity.BlockEntity;
import blocks.impl.builder.OriginBlock;
import blocks.impl.data.account.BlockAccount;
import blocks.impl.event.OriginBreakEvent;
import blocks.impl.event.OriginRegenerationEvent;
import commons.events.api.EventRegistry;
import commons.events.impl.impl.DetachedSubscriber;
import me.lucko.helper.Schedulers;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class FarmingEvents {

	private static DetachedSubscriber<OriginRegenerationEvent> regenEvent;
	private static DetachedSubscriber<OriginBreakEvent> breakEvent;
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
			System.out.println("Hi");

			if (blockData instanceof Ageable ageable) {
				System.out.println("Hi");
				if (ageable.getAge() == ageable.getMaximumAge()) {
					final OriginBreakEvent originBreakEvent = new OriginBreakEvent(block, event.getPlayer());
					originBreakEvent.callEvent();
					event.setCancelled(true);
				}
			}
		});
	}

	private static void initBreak() {
		breakEvent = new DetachedSubscriber<>(OriginBreakEvent.class, (context, event) -> {
			Player player = event.getPlayer();
			Block block = event.getBlock();

			final BlockAccount account = plugin.getAccounts().getAccount(player);
			if (account == null) return;

			final ProgressRegistry progressRegistry = account.getProgressRegistry();

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
			System.out.println("Hi");

			final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();

			final Regenable regenable = (Regenable) originBlock.getAspects().get(AspectType.REGENABLE);
			final FakeBlock fake = projectable.toFakeBlock(originBlock.getBlockLocation());
			regenable.setFakeBlock(fake);

			final long endTime = (long) (System.currentTimeMillis() + regenable.getRegenTime() * 1000);
			((Ageable)block.getBlockData()).setAge(0);

			regenerationRegistry.createRegen(regenable, block, player, endTime);
		});
	}

	private static void initRegen() {
		regenEvent = new DetachedSubscriber<>(OriginRegenerationEvent.class, ((context, event) -> {
			System.out.println("Regen Called");
			final BlockAccount account = plugin.getAccounts().getAccount(event.getPlayer());
			if (account == null) return;

			final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();
			final Regenable regenBlock = event.getRegenable();
			final Player player = event.getPlayer();
			final Block block = event.getBlock();
			final BlockData blockData = block.getBlockData();
			final FakeBlock fakeBlock = regenBlock.getFakeBlock();
			final IllusionRegistry illusionRegistry = event.getIllusionsAPI().localRegistry(player);
			final Location location = regenBlock.getFakeBlock().getBlockLocation();
			final Projectable projectable = (Projectable) regenBlock.getEditor().getAspects().get(AspectType.PROJECTABLE);
			final BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			final Ageable ageable = ((Ageable) blockData);
			ageable.setAge(0);

			illusionRegistry.register(fakeBlock);
			player.sendBlockChange(location, fakeBlock.getProjectedBlockData());

			Schedulers.bukkit().runTaskTimer(plugin, bukkitTask -> {
				if (System.currentTimeMillis() >= event.getEnd()) {
					Schedulers.bukkit().runTaskTimer(plugin, runnable -> {
						if (ageable.getAge() < ageable.getMaximumAge()) {
							ageable.setAge(ageable.getAge() + 1);
							illusionRegistry.unregister(regenBlock.getFakeBlock());

							projectable.setProjectedBlockData(ageable);
							regenBlock.setFakeBlock(projectable.toFakeBlock(location));

							illusionRegistry.register(regenBlock.getFakeBlock());
							player.sendBlockChange(location, projectable.getProjectedBlockData());
						} else if (ageable.getMaximumAge() == ageable.getAge()) {
							illusionRegistry.unregister(regenBlock.getFakeBlock());
							regenerationRegistry.deleteRegen(regenBlock);
							runnable.cancel();
							bukkitTask.cancel();
						}
					}, 10, 10);
				}
			}, 0, 5);
		}));
	}

	private static VarHandle unreflect(Class<?> clazz, String name) {
		Field field;
		try {
			field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return MethodHandles.privateLookupIn(clazz, MethodHandles.lookup()).unreflectVarHandle(field);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
