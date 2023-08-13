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
import blocks.impl.aspect.effect.type.OriginParticle;
import blocks.impl.aspect.effect.type.OriginSound;
import blocks.impl.builder.OriginBlock;
import blocks.impl.data.account.BlockAccount;
import blocks.impl.events.BreakEvent;
import blocks.impl.events.RegenEvent;
import commons.events.api.EventRegistry;
import commons.events.impl.impl.DetachedSubscriber;
import commons.hologram.InterpolatedHologram;
import commons.interpolation.impl.InterpolationType;
import commons.math.MathUtils;
import farming.impl.settings.FarmingSettings;
import me.lucko.helper.Schedulers;
import me.lucko.helper.serialize.Position;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("FieldCanBeLocal")
public class FarmingEvents {

	private static DetachedSubscriber<RegenEvent> regenEvent;
	private static DetachedSubscriber<BreakEvent> breakEvent;
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
			final Player player = event.getPlayer();
			final BlockAccount account = BlocksPlugin.get().getInstance(BlocksPlugin.class).getAccountStorage().getAccount(player);
			final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();

			if (blockData instanceof Ageable ageable) {
				if (BlocksAPI.inRegion(block.getLocation())) {
					if (regenerationRegistry.getRegenerations().containsKey(CraftLocation.toBlockPosition(block.getLocation()))) {
						event.setCancelled(true);
						return;
					}
					if (ageable.getAge() == ageable.getMaximumAge()) {
						// if (!OriginHoe.isHoe(player.getInventory().getItemInMainHand())) return;
						new BreakEvent("farming", block, event.getPlayer(), false).callEvent();
						event.setCancelled(true);
					}
				}
			}
		});
	}

	private static void initBreak() {
		breakEvent = new DetachedSubscriber<>(BreakEvent.class, (context, event) -> {
			if (!event.getCalling().equals("farming")) return;
			Player player = event.getPlayer();
			Block block = event.getBlock();

			final BlockAccount account = plugin.getAccounts().getAccount(player);
			if (account == null) return;

			final World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
			final OriginBlock originBlock = (OriginBlock) BlocksAPI.getBlock(block.getLocation());

			if (originBlock == null) return;

			final Effectable effectable = (Effectable) originBlock.getAspects().get(AspectType.EFFECTABLE);
			final Dropable dropable = (Dropable) originBlock.getAspects().get(AspectType.DROPABLE);
			final Projectable projectable = (Projectable) originBlock.getAspects().get(AspectType.PROJECTABLE);

			final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();
			if (regenerationRegistry.getRegenerations().containsKey(CraftLocation.toBlockPosition(block.getLocation())))
				return;

			final FakeBlock fake = projectable.toFakeBlock(originBlock.getBlockLocation());
			final Regenable regenable = (Regenable) originBlock.getAspects().get(AspectType.REGENABLE);

			if (regenable == null) return;
			if (regenable.getRegenTime() <= 0) return;
			regenable.setFakeBlock(fake);

			final long endTime = (long) (System.currentTimeMillis() + regenable.getRegenTime() * 1000);

			regenerationRegistry.createRegen(regenable, block);
			new RegenEvent("farming", regenable, player, block, endTime).callEvent();

			final List<ItemStack> clonedDrops = new ArrayList<>(block.getDrops());
			if (clonedDrops.isEmpty()) {
				return;
			}

			block.getDrops().clear();
//
//			if (OriginHoe.isHoe(player.getInventory().getItemInMainHand())) {
//				OriginHoe.updateBlocksBroken(player.getInventory().getItemInMainHand(), 1);
//			}


			if (!event.isCalledFromEnchant()) {
				if (effectable != null) {
					effectable.getEffects().forEach(effect -> {
						if (effect.getEffectType() instanceof OriginParticle) {
							if (FarmingSettings.CUSTOM_BLOCK_PARTICLES.isEnabled(player)) {
								System.out.println("ENABLED");
								effect.getEffectType().handleEffect(player, block.getLocation());
							}
						}
						if (effect.getEffectType() instanceof OriginSound) {
							if (FarmingSettings.CUSTOM_BLOCK_SOUNDS.isEnabled(player)) {
								System.out.println("ENABLED");
								effect.getEffectType().handleEffect(player, block.getLocation());
							}
						}
					});
				}
			}

			if (dropable != null) {
				dropable.getDrops().forEach(drop -> {
					// TODO: ITEM BACKPACKS?????
					new BlockEntity(player, nmsWorld, block, drop, true);
				});
			}

			final Position position = Position.of(block.getLocation().clone().set(block.getX(), block.getY() + 1, block.getZ()));
			final float xIncrementation = MathUtils.random(-3.5f, 3.5f);
			final float yIncrementation = 2.7f;
			final float zIncrementation = MathUtils.random(-3.5f, 3.5f);

			final double xp = MathUtils.random(2, 8);

			if (!event.isCalledFromEnchant()) {
				final InterpolatedHologram hologram = new InterpolatedHologram(block, position,
						StringPlaceholder.of("xp", String.valueOf(xp)), "&eYou just gained {xp} experience!");

				hologram.create(player,
						27,
						20,
						xIncrementation,
						yIncrementation,
						zIncrementation,
						true,
						true,
						true,
						InterpolationType.circle);
			}


			// Commons.commons().getAccounts().getAccount(player).addExperience(BigDecimal.valueOf(xp));
		});
	}

	private static void initRegen() {
		regenEvent = new DetachedSubscriber<>(RegenEvent.class, ((context, event) -> {
			if (!event.getCalling().equals("farming")) return;

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
