package mining.event;

import blocks.BlocksAPI;
import blocks.block.aspects.AspectType;
import blocks.block.aspects.drop.Dropable;
import blocks.block.aspects.effect.Effectable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.progress.registry.ProgressRegistry;
import blocks.impl.anim.entity.BlockEntity;
import blocks.impl.builder.OriginBlock;
import blocks.impl.event.OriginBreakEvent;
import commons.events.api.EventRegistry;
import commons.events.impl.impl.DetachedSubscriber;
import commons.events.impl.impl.PacketEventListener;
import commons.util.reflect.FieldAccess;
import commons.util.reflect.Reflection;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.level.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
public class MiningBreakEvent {

	private static final ProgressRegistry progressRegistry = BlocksAPI.getInstance().getProgressRegistry();
	private static final RegenerationRegistry regenRegistry = BlocksAPI.getInstance().getRegenerationRegistry();
	private static DetachedSubscriber<PacketPlayInBlockDig> packetEventSubscriber;
	private static DetachedSubscriber<OriginBreakEvent> bukkitEventSubscriber;

	private static final FieldAccess<PacketPlayInBlockDig.EnumPlayerDigType> enumPlayerDigTypeInDig = Reflection.unreflectFieldAccess(PacketPlayInBlockDig.class, "c");
	private static final FieldAccess<BlockPosition> blockPositionDigIn = Reflection.unreflectFieldAccess(PacketPlayInBlockDig.class, "a");

	public static void init(EventRegistry registry) {
		packetEventSubscriber = new DetachedSubscriber<>(PacketPlayInBlockDig.class, ((context, event) -> {
			if (context.getPlayer().getTargetBlockExact(5) == null) return;
			if (BlocksAPI.inRegion(context.getPlayer().getTargetBlockExact(5).getLocation())) {
				BlockPosition blockPos = blockPositionDigIn.get(event);

				if (regenRegistry.getRegenerations().containsKey(blockPos)) return;

				PacketPlayOutEntityEffect entityEffect = new PacketPlayOutEntityEffect(context.getPlayer().getEntityId(), new MobEffect(Objects.requireNonNull(MobEffectList.a(PotionEffectType.SLOW_DIGGING.getId())), Integer.MAX_VALUE, 255, true, false));
				PacketEventListener.sendPacket(context.getPlayer(), entityEffect);
				progressRegistry.getBlocksBreaking().remove(blockPos);

				PacketPlayInBlockDig.EnumPlayerDigType digType = enumPlayerDigTypeInDig.get(event);
				if (digType == PacketPlayInBlockDig.EnumPlayerDigType.a) {
					progressRegistry.getBlocksBreaking().put(blockPos, true);
				} else if (digType == PacketPlayInBlockDig.EnumPlayerDigType.c || digType == PacketPlayInBlockDig.EnumPlayerDigType.b) {
					progressRegistry.getBlocksBreaking().remove(blockPos);
					progressRegistry.copyOldData(blockPos);
				}
			}
		}));

		bukkitEventSubscriber = new DetachedSubscriber<>(OriginBreakEvent.class, (context, event) -> {
			Player player = event.getPlayer();
			Block block = event.getBlock();

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
				effectable.getEffects().forEach(effect -> effect.getEffectType().handleEffect(player, block.getLocation(), true));
			}

			if (dropable != null) {
				new BlockEntity(player, nmsWorld, block, dropable.getDrops().get(0));
			}

			progressRegistry.resetAll(new BlockPosition(block.getX(), block.getY(), block.getZ()));
		});

		bukkitEventSubscriber.bind(registry);
		packetEventSubscriber.bind(registry);
	}
}
