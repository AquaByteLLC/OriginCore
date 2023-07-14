package mining.impl.events;

import blocks.BlocksAPI;
import blocks.block.aspects.AspectType;
import blocks.block.aspects.drop.Dropable;
import blocks.block.aspects.effect.Effectable;
import blocks.block.aspects.projection.Projectable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionFactory;
import blocks.block.illusions.IllusionRegistry;
import blocks.block.progress.registry.ProgressRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.anim.entity.BlockEntity;
import blocks.impl.builder.OriginBlock;
import blocks.impl.data.account.BlockAccount;
import blocks.impl.events.BreakEvent;
import blocks.impl.events.RegenEvent;
import commons.events.api.EventRegistry;
import commons.events.impl.impl.DetachedSubscriber;
import commons.events.impl.impl.PacketEventListener;
import commons.util.reflect.FieldAccess;
import commons.util.reflect.Reflection;
import me.lucko.helper.Schedulers;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("FieldCanBeLocal")
public class MiningEvents {

	private static DetachedSubscriber<PacketPlayInBlockDig> digInPacket;
	private static DetachedSubscriber<RegenEvent> regenEvent;
	private static DetachedSubscriber<BreakEvent> breakEvent;
	private static final FieldAccess<PacketPlayInBlockDig.EnumPlayerDigType> c = Reflection.unreflectFieldAccess(PacketPlayInBlockDig.class, "c");
	private static final FieldAccess<BlockPosition> a = Reflection.unreflectFieldAccess(PacketPlayInBlockDig.class, "a");
	private static final BlocksPlugin plugin = BlocksPlugin.get().getInstance(BlocksPlugin.class);

	public static void init(EventRegistry registry) {
		initDig();
		initBreak();
		initRegen();

		breakEvent.bind(registry);
		digInPacket.bind(registry);
		regenEvent.bind(registry);
	}

	private static void initDig() {
		digInPacket = new DetachedSubscriber<>(PacketPlayInBlockDig.class, ((context, event) -> {
			if (context.getPlayer().getTargetBlockExact(5) == null) return;
			if (BlocksAPI.inRegion(context.getPlayer().getTargetBlockExact(5).getLocation())) {

				final BlockAccount account = plugin.getAccounts().getAccount(context.getPlayer());
				final ProgressRegistry progressRegistry = account.getProgressRegistry();
				final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();

				final BlockPosition blockPos = a.get(event);

				if (regenerationRegistry.getRegenerations().containsKey(blockPos)) return;

				PacketPlayOutEntityEffect entityEffect = new PacketPlayOutEntityEffect(context.getPlayer().getEntityId(), new MobEffect(Objects.requireNonNull(MobEffectList.a(PotionEffectType.SLOW_DIGGING.getId())), Integer.MAX_VALUE, 255, true, false));
				PacketEventListener.sendPacket(context.getPlayer(), entityEffect);
				progressRegistry.getBlocksBreaking().remove(blockPos);
				if (c.get(event) == PacketPlayInBlockDig.EnumPlayerDigType.a) {
					progressRegistry.getBlocksBreaking().put(blockPos, true);
				} else if (c.get(event) == PacketPlayInBlockDig.EnumPlayerDigType.c || c.get(event) == PacketPlayInBlockDig.EnumPlayerDigType.b) {
					progressRegistry.getBlocksBreaking().remove(blockPos);
					progressRegistry.copyOldData(blockPos);
				}
			}
		}));
	}

	private static void initBreak() {
		breakEvent = new DetachedSubscriber<>(BreakEvent.class, (context, event) -> {
			if (!event.getCalling().equals("mining")) return;
			Player player = event.getPlayer();
			Block block = event.getBlock();

			final BlockAccount account = plugin.getAccounts().getAccount(player);
			if (account == null) return;

			final ProgressRegistry progressRegistry = account.getProgressRegistry();
			final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();

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

			if (effectable != null) {
				effectable.getEffects().forEach(effect ->
						effect.getEffectType().handleEffect(player, block.getLocation()));
			}

			if (dropable != null) {
				dropable.getDrops().forEach(drop -> {
					// TODO: ITEM BACKPACKS?????
					new BlockEntity(player, nmsWorld, block, drop, true);
				});

				Projectable projectable = (Projectable) originBlock.getAspects().get(AspectType.PROJECTABLE);

				if (projectable == null) return;
				if (projectable.getProjectedBlockData() == null) return;

				FakeBlock fake = projectable.toFakeBlock(originBlock.getBlockLocation());
				Regenable regenable = (Regenable) originBlock.getAspects().get(AspectType.REGENABLE);

				if (regenable == null) return;
				if (regenable.getRegenTime() <= 0) return;

				regenable.setFakeBlock(fake);

				long endTime = (long) (System.currentTimeMillis() + regenable.getRegenTime() * 1000);
				regenerationRegistry.createRegen(regenable, block);
				new RegenEvent("mining", regenable, player, block, endTime).callEvent();
			}

			final BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());

			progressRegistry.resetAll(blockPosition);

			int randomInts = progressRegistry.getRandomIntegers()
					.get(blockPosition) != null ? progressRegistry.getRandomIntegers().get(blockPosition) : new Random().nextInt(999999999);

			PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(randomInts, blockPosition, -1);
			PacketEventListener.sendPacket(player, packet);
		});
	}

	private static void initRegen() {
		regenEvent = new DetachedSubscriber<>(RegenEvent.class, ((context, event) -> {
			if (!event.getCalling().equals("mining")) return;
			final BlockAccount account = plugin.getAccounts().getAccount(event.getPlayer());
			if (account == null) return;

			final ProgressRegistry progressRegistry = account.getProgressRegistry();
			final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();
			final Regenable regenBlock = event.getRegenable();
			final Player player = event.getPlayer();
			final Block block = event.getBlock();
			final BlockData blockData = block.getBlockData();
			final FakeBlock fakeBlock = regenBlock.getFakeBlock();
			final IllusionRegistry illusionRegistry = event.getIllusionsAPI().localRegistry(player);
			final IllusionFactory illusionFactory = event.getIllusionsAPI().factory();
			final Location location = regenBlock.getFakeBlock().getBlockLocation();
			final Projectable projectable = (Projectable) regenBlock.getEditor().getAspects().get(AspectType.PROJECTABLE);
			final BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());

			illusionRegistry.register(fakeBlock);
			player.sendBlockChange(location, fakeBlock.getProjectedBlockData());

			Schedulers.bukkit().runTaskTimer(plugin, bukkitTask -> {
				if (System.currentTimeMillis() >= event.getEnd()) {
					regenerationRegistry.deleteRegen(block);
					illusionRegistry.unregister(fakeBlock);
					player.sendBlockChange(location, blockData);
					bukkitTask.cancel();
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
