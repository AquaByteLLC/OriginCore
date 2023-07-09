package blocks.impl.protect;

import blocks.block.protect.ProtectedBlock;
import blocks.block.protect.ProtectedObject;
import blocks.block.protect.ProtectedRegion;
import commons.Commons;
import commons.events.api.Subscribe;
import commons.util.PackUtil;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityEnterBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author vadim
 */
@SuppressWarnings("unused")
class Guard {

	private final World world;

	final Long2ObjectMap<ProtectedBlock> blocks = new Long2ObjectOpenHashMap<>(500);
	final ObjectList<ProtectedRegion> regions = new ObjectHashList<>(10);

	Guard(UUID world) {
		this.world = Bukkit.getWorld(world);
		Commons.events().subscribeAll(this);
	}

	private boolean inWorld(Block block) {
		return block != null && world.getUID().equals(block.getWorld().getUID());
	}

	public final ObjectList<ProtectedRegion> getRegionsAt(Block block) {
		if (!inWorld(block))
			return ObjectLists.emptyList();

		int x, y, z;
		x = block.getX();
		y = block.getY();
		z = block.getZ();

		ObjectList<ProtectedRegion> list = new ObjectArrayList<>(10);
		for (ProtectedRegion region : regions)
			if (region.getBounds().contains(x, y, z))
				list.add(region);

		return list;
	}

	public final ProtectedBlock getBlockAt(Block block) {
		if (!inWorld(block))
			return null;
		return blocks.get(PackUtil.packLoc(block.getX(), block.getY(), block.getZ()));
	}

	private static void handle(ProtectedObject prot, EventWrapper<?> wrapper) {
		boolean notOp = wrapper.entity == null || !wrapper.entity.isOp();
		switch (prot.getProtectionStrategy()) {
			case OVERRIDE_ON -> { wrapper.event.setCancelled(true); }
			case DEFAULT -> {
				if (notOp)
					wrapper.event.setCancelled(true);
			}
			case OVERRIDE_OFF -> { /* NOP */ }
		}
	}

	private void protect(EventWrapper<?> wrapper) {
		boolean handled = false;
		for (Block block : wrapper.blocks) {
			if (block == null) continue;
			ProtectedBlock prot = getBlockAt(block);
			if (prot == null) continue;

			handled = true;
			handle(prot, wrapper);
		}

		// blocks take affinity
		if (handled)
			return;

		for (Block block : wrapper.blocks) {
			if (block == null) continue;
			ObjectList<ProtectedRegion> regions = getRegionsAt(block);
			if (regions.isEmpty()) continue;

			// pick highest priority region
			ProtectedRegion prot = null;
			int             prio = Integer.MIN_VALUE;
			for (ProtectedRegion region : regions)
				if (region.getPriority() > prio) {
					prio = region.getPriority();
					prot = region;
				}

			assert prot != null; // regions is not empty, therefore the loop will have found at least one
			handle(prot, wrapper);
		}
	}

	private static List<Block> combine(List<Block> blocks, Block... block) {
		List<Block> list = new ArrayList<>(blocks);
		list.addAll(Arrays.asList(block));
		return list;
	}

	private static final class EventWrapper<E extends Event & Cancellable> {

		E event;
		Entity entity;
		List<Block> blocks;

		EventWrapper(E event, Entity entity, Block... block) {
			this.event  = event;
			this.entity = entity;
			this.blocks = Arrays.asList(block);
		}

		EventWrapper(E event, Block... block) {
			this.event  = event;
			this.entity = null;
			this.blocks = Arrays.asList(block);
		}

		EventWrapper(E event, Entity entity, List<Block> blocks) {
			this.event  = event;
			this.entity = entity;
			this.blocks = blocks;
		}

		EventWrapper(E event, Block block, List<Block> blocks) {
			this.event  = event;
			this.blocks = combine(blocks, block);
		}

		EventWrapper(E event, Entity entity, Block block, List<Block> blocks) {
			this.event  = event;
			this.entity = entity;
			this.blocks = combine(blocks, block);
		}

		Player getPlayer() {
			if (entity instanceof Player player)
				return player;
			else
				return null;
		}

	}

	private static class SpecialCancellable extends Event implements Cancellable {

		private final Supplier<Boolean> isCancelled;
		private final Consumer<Boolean> setCancelled;

		SpecialCancellable(Supplier<Boolean> isCancelled, Consumer<Boolean> setCancelled) {
			this.isCancelled  = isCancelled;
			this.setCancelled = setCancelled;
		}

		@Override
		public boolean isCancelled() {
			return isCancelled.get();
		}

		@Override
		public void setCancelled(boolean cancel) {
			setCancelled.accept(cancel);
		}

		@Override
		public @NotNull HandlerList getHandlers() {
			return new HandlerList();
		}

	}

	@Subscribe
	void onBellRing(BellRingEvent event) {
		protect(new EventWrapper<>(event, event.getEntity(), event.getBlock()));
	}

	@Subscribe
	void onBurn(BlockBurnEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onCanBuild(BlockCanBuildEvent event) {
		protect(new EventWrapper<>(new SpecialCancellable(event::isBuildable, event::setBuildable), event.getPlayer(), event.getBlock()));
	}

	@Subscribe
	void onCook(BlockCookEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onDamage(BlockDamageEvent event) {
		protect(new EventWrapper<>(event, event.getPlayer(), event.getBlock()));
	}

	@Subscribe
	void onDispense(BlockDispenseEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onDropItem(BlockDropItemEvent event) {
		protect(new EventWrapper<>(event, event.getPlayer(), event.getBlock()));
	}

	@Subscribe
	void onExp(BlockExpEvent event) {
		protect(new EventWrapper<>(new SpecialCancellable(() -> false, (cancel) -> {
			if (cancel)
				event.setExpToDrop(0);
		}), event.getBlock()));
	}

	@Subscribe
	void onExplode(BlockExplodeEvent event) {
		protect(new EventWrapper<>(event, event.getBlock(), event.blockList()));
	}

	@Subscribe
	void onExplode(EntityExplodeEvent event) {
		protect(new EventWrapper<>(event, event.getLocation().getBlock(), event.blockList()));
	}

	@Subscribe
	void onFade(BlockFadeEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onFertilize(BlockFertilizeEvent event) {
		protect(new EventWrapper<>(event, event.getPlayer(), event.getBlock(), event.getBlocks().stream().map(BlockState::getBlock).toList()));
	}

	@Subscribe
	void onFromTo(BlockFromToEvent event) {
		protect(new EventWrapper<>(event, event.getBlock(), event.getToBlock()));
	}

	@Subscribe
	void onGrow(BlockGrowEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onIgnite(BlockIgniteEvent event) {
		protect(new EventWrapper<>(event, event.getPlayer(), event.getBlock()));
	}

	// removed -- performance (besides, this event doesn't even seem to be doing anything)
//	@Subscribe
//	void onPhysics(BlockPhysicsEvent event) {
//		protect(new EventWrapper<>(event, event.getBlock(), event.getSourceBlock()));
//	}

	@Subscribe
	void onPistonExtend(BlockPistonExtendEvent event) {
		List<Block> blocks = new ArrayList<>(event.getBlocks());
		blocks.add(event.getBlock().getRelative(((Directional) event.getBlock().getBlockData()).getFacing())); // the piston head
		protect(new EventWrapper<>(event, event.getBlock(), blocks));
	}

	@Subscribe
	void onPistonsRetract(BlockPistonRetractEvent event) {
		List<Block> blocks = new ArrayList<>(event.getBlocks());
		blocks.add(event.getBlock().getRelative(((Directional) event.getBlock().getBlockData()).getFacing())); // the piston head
		protect(new EventWrapper<>(event, event.getBlock(), blocks));
	}

	@Subscribe
	void onPlace(BlockPlaceEvent event) {
		protect(new EventWrapper<>(event, event.getPlayer(), event.getBlock(), event.getBlockAgainst(), event.getBlockPlaced()));
	}

	@Subscribe
	void onBreak(BlockBreakEvent event) {
		protect(new EventWrapper<>(event, event.getPlayer(), event.getBlock()));
	}

	@Subscribe
	void onReceiveGame(BlockReceiveGameEvent event) {
		protect(new EventWrapper<>(event, event.getEntity(), event.getBlock()));
	}

	@Subscribe
	void onRedstone(BlockRedstoneEvent event) {
		protect(new EventWrapper<>(new SpecialCancellable(() -> false, cancel -> {
			if (cancel)
				event.setNewCurrent(0);
		}), event.getBlock()));
	}

	@Subscribe
	void onShearEntity(BlockShearEntityEvent event) {
		protect(new EventWrapper<>(event, event.getEntity(), event.getBlock()));
	}

	@Subscribe
	void onBrew(BrewEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onBrewingStandFuel(BrewingStandFuelEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onCauldronLevelChange(CauldronLevelChangeEvent event) {
		protect(new EventWrapper<>(event, event.getEntity(), event.getBlock()));
	}

	@Subscribe
	void onFluidLevelChange(FluidLevelChangeEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onFurnaceBurn(FurnaceBurnEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onLeavesDecay(LeavesDecayEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onMoistureChange(MoistureChangeEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onNotePlay(NotePlayEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onSignChange(SignChangeEvent event) {
		protect(new EventWrapper<>(event, event.getPlayer(), event.getBlock()));
	}

	@Subscribe
	void onSpongeAbsorb(SpongeAbsorbEvent event) {
		protect(new EventWrapper<>(event, event.getBlock(), event.getBlocks().stream().map(BlockState::getBlock).toList()));
	}

	@Subscribe
	void onTNTPrime(TNTPrimeEvent event) {
		protect(new EventWrapper<>(event, event.getPrimingEntity(), event.getBlock(), event.getPrimingBlock()));
	}

	@Subscribe
	void onBlockForm(EntityBlockFormEvent event) {
		protect(new EventWrapper<>(event, event.getEntity(), event.getBlock()));
	}

	@Subscribe
	void onChangeBlock(EntityChangeBlockEvent event) {
		protect(new EventWrapper<>(event, event.getEntity(), event.getBlock()));
	}

	@Subscribe
	void onEnterBlock(EntityEnterBlockEvent event) {
		protect(new EventWrapper<>(event, event.getEntity(), event.getBlock()));
	}

	@Subscribe
	void onDamageByBlock(EntityDamageByBlockEvent event) {
		protect(new EventWrapper<>(event, event.getEntity(), event.getDamager()));
	}

	@Subscribe
	void onCombustByBlock(EntityCombustByBlockEvent event) {
		protect(new EventWrapper<>(event, event.getEntity(), event.getCombuster()));
	}

	@Subscribe
	void onInsideBlock(EntityInsideBlockEvent event) {
		protect(new EventWrapper<>(event, event.getEntity(), event.getBlock()));
	}

}
