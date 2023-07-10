package blocks.impl.protect;

import blocks.block.aspects.location.BlockLike;
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
import org.bukkit.Location;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author vadim
 */
@SuppressWarnings("unused")
class Guard {

	private final World world;

	final Long2ObjectMap<ProtectedBlock> blocks = new Long2ObjectOpenHashMap<>(500);
	final ObjectList<ProtectedRegion> regions = new ObjectArrayList<>(10);

	final ReadWriteLock lock = new ReentrantReadWriteLock();

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

		lock.readLock().lock();
		try {
			for (ProtectedRegion region : regions)
				if (region.getBounds().contains(x, y, z))
					list.add(region);
		} finally {
			lock.readLock().unlock();
		}

		return list;
	}

	public final ProtectedBlock getBlockAt(Block block) {
		if (!inWorld(block))
			return null;

		ProtectedBlock result;

		lock.readLock().lock();
		try {
			result = blocks.get(PackUtil.packLoc(block.getX(), block.getY(), block.getZ()));
		} finally {
			lock.readLock().unlock();
		}

		return result;
	}

	public final ProtectedObject getProtectionAt(Block block) {
		if (!inWorld(block))
			return null;

		ProtectedObject prot;

		// blocks take priority over regions
		prot = getBlockAt(block);
		if (prot != null)
			return prot;

		ObjectList<ProtectedRegion> regions = getRegionsAt(block);
		if (regions.isEmpty())
			return null;

		// pick highest priority region
		int prio = Integer.MIN_VALUE;
		for (ProtectedRegion region : regions)
			if (region.getPriority() > prio) {
				prio = region.getPriority();
				prot = region;
			}

		return prot;
	}

	private void protect(EventWrapper<?> wrapper) {
		boolean notOp = wrapper.entity == null || !wrapper.entity.isOp();

		Iterator<EventBlock> iterator = wrapper.blocks.iterator();

		ProtAction action = new ProtAction(wrapper.event); // use mutable object instead of allocating a new one each iteration
		EventBlock block;
		while (iterator.hasNext()) {
			block = iterator.next();
			if (block == null)
				continue;

			ProtectedObject prot = getProtectionAt(block.getBlock());
			if (prot == null)
				continue;

			action.prot = prot;
			action.block = block.getBlock();
			action.entity = wrapper.entity;

			if (!prot.getProtectionStrategy().permits(action)) {
				if (wrapper.blocks.isMutable())
					iterator.remove(); // in some events, you can remove a block from the event, instead of cancelling the whole event
				else
					wrapper.event.setCancelled(true);
			}
		}
	}

	// holy bad code

	@SuppressWarnings("DataFlowIssue")
	private static final class EventBlock implements BlockLike {

		private final Block block;
		private final BlockState state;

		EventBlock(BlockState state) {
			this.block = null;
			this.state = state;
		}

		EventBlock(Block block) {
			this.block = block;
			this.state = null;
		}

		public BlockState getState() {
			return state == null ? block.getState() : state;
		}

		@Override
		public Block getBlock() {
			return block == null ? state.getBlock() : block;
		}

		@Override
		public Location getBlockLocation() {
			return getBlock().getLocation();
		}

	}

	@SuppressWarnings("rawtypes,unchecked")
	private static class BlockList extends ArrayList<EventBlock> {

		public static final int MUT_BLOCK = 1;
		public static final int MUT_STATE = 2;
		public static final int IMMUTABLE = 3;

		private final List backing;
		private final int mode;

		private BlockList(List backing, int mode) {
			super();
			this.backing = backing;
			this.mode    = mode;
			if (mode != MUT_BLOCK && mode != MUT_STATE && mode != IMMUTABLE) throw new IllegalArgumentException("illegal mode " + mode);
		}

		public static BlockList ofBlocks(List<Block> blocks) {
			return new BlockList(blocks, MUT_BLOCK);
		}

		public static BlockList ofStates(List<BlockState> states) {
			return new BlockList(states, MUT_STATE);
		}

		public static BlockList ofImmutable(List<?> list) {
			return new BlockList(list, IMMUTABLE);
		}

		public boolean isMutable() {
			return mode != IMMUTABLE;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean contains(Object o) {
			return backing.contains(o);
		}

		@Override
		public EventBlock get(int index) {
			Object o = backing.get(index);
			if (o instanceof Block block)
				return new EventBlock(block);
			if (o instanceof BlockState state)
				return new EventBlock(state);
			throw new IllegalStateException();
		}

		@NotNull
		@Override
		public ListIterator<EventBlock> listIterator(int index) {
			throw new UnsupportedOperationException();
		}

		@NotNull
		@Override
		public ListIterator<EventBlock> listIterator() {
			throw new UnsupportedOperationException();
		}

		@NotNull
		@Override
		public Iterator<EventBlock> iterator() {
			return new Iterator<EventBlock>() {
				Iterator backing = BlockList.this.backing.iterator();

				@Override
				public boolean hasNext() {
					return backing.hasNext();
				}

				@Override
				public EventBlock next() {
					Object o = backing.next();
					if (o instanceof Block block)
						return new EventBlock(block);
					if (o instanceof BlockState state)
						return new EventBlock(state);
					throw new IllegalStateException();
				}

				@Override
				public void remove() {
					backing.remove();
				}
			};
		}

		@Override
		public boolean add(EventBlock eventBlock) {
			if (!isMutable()) throw new UnsupportedOperationException();
			return backing.add(mode == MUT_BLOCK ? eventBlock.getBlock() : eventBlock.getState());
		}

		@Override
		public boolean remove(Object o) {
			if (!isMutable()) throw new UnsupportedOperationException();
			EventBlock eventBlock = (EventBlock) o;
			return backing.remove(mode == MUT_BLOCK ? eventBlock.getBlock() : eventBlock.getState());
		}

	}

	private static final class EventWrapper<E extends Event & Cancellable> {

		final E event;
		final Entity entity;
		final BlockList blocks;
		final Block cause; // the block that triggered this event

		EventWrapper(E event, Entity entity, Block... block) {
			this.event  = event;
			this.entity = entity;
			this.blocks = BlockList.ofImmutable(Arrays.asList(block));
			this.cause  = block[0];
		}

		EventWrapper(E event, Block... block) {
			this.event  = event;
			this.entity = null;
			this.blocks = BlockList.ofImmutable(Arrays.asList(block));
			this.cause  = block[0];
		}

		EventWrapper(E event, Block cause, BlockList blockList) {
			this.event  = event;
			this.entity = null;
			this.cause  = cause;
			this.blocks = blockList;
		}

		EventWrapper(E event, Entity entity, Block cause, BlockList blockList) {
			this.event  = event;
			this.entity = entity;
			this.cause  = cause;
			this.blocks = blockList;
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
		protect(new EventWrapper<>(event, event.getBlock(), BlockList.ofBlocks(event.blockList())));
	}

	@Subscribe
	void onExplode(EntityExplodeEvent event) {
		protect(new EventWrapper<>(event, event.getLocation().getBlock(), BlockList.ofBlocks(event.blockList())));
	}

	@Subscribe
	void onFade(BlockFadeEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
	}

	@Subscribe
	void onFertilize(BlockFertilizeEvent event) {
		protect(new EventWrapper<>(event, event.getPlayer(), event.getBlock(), BlockList.ofStates(event.getBlocks())));
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

	// removed -- performance (besides, cancelling this event doesn't even seem to be doing anything)
//	@Subscribe
//	void onPhysics(BlockPhysicsEvent event) {
//		protect(new EventWrapper<>(event, event.getBlock(), event.getSourceBlock()));
//	}

	@Subscribe
	void onPistonExtend(BlockPistonExtendEvent event) {
		List<Block> blocks = new ArrayList<>(event.getBlocks());
		blocks.add(event.getBlock().getRelative(((Directional) event.getBlock().getBlockData()).getFacing())); // the piston head
		protect(new EventWrapper<>(event, event.getBlock(), BlockList.ofImmutable(blocks)));
	}

	@Subscribe
	void onPistonsRetract(BlockPistonRetractEvent event) {
		List<Block> blocks = new ArrayList<>(event.getBlocks());
		blocks.add(event.getBlock().getRelative(((Directional) event.getBlock().getBlockData()).getFacing())); // the piston head
		protect(new EventWrapper<>(event, event.getBlock(), BlockList.ofImmutable(blocks)));
	}

	@Subscribe
	void onPlace(BlockPlaceEvent event) {
		protect(new EventWrapper<>(event, event.getPlayer(), event.getBlock(), event.getBlockPlaced()));
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
		protect(new EventWrapper<>(event, event.getBlock(), BlockList.ofStates(event.getBlocks())));
	}

	@Subscribe
	void onTNTPrime(TNTPrimeEvent event) {
		protect(new EventWrapper<>(event, event.getPrimingEntity(), event.getBlock(), event.getPrimingBlock()));
	}

	@Subscribe
	void onSpread(BlockSpreadEvent event) {
		protect(new EventWrapper<>(event, event.getBlock(), event.getSource()));
	}

	@Subscribe
	void onForm(BlockFormEvent event) {
		protect(new EventWrapper<>(event, event.getBlock()));
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

	@Subscribe
	void onInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL) // trample
			protect(new EventWrapper<>(event, event.getPlayer(), event.getClickedBlock()));
	}

}
