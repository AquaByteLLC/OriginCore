package blocks.impl.registry;

import blocks.block.aspects.illusions.FakeBlock;
import blocks.block.aspects.illusions.registry.IllusionRegistry;
import blocks.block.aspects.overlay.Overlayable;
import blocks.block.aspects.overlay.registry.OverlayLocationRegistry;
import blocks.impl.aspect.AspectEnum;
import commons.events.api.EventRegistry;
import commons.events.api.PlayerEventContext;
import commons.events.api.Subscribe;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutMultiBlockChange;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftVector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.HashMap;

public class IllusionRegistryImpl implements IllusionRegistry {
	private final OverlayLocationRegistry overlays;
	private final HashMap<Location, FakeBlock> blockMap;
	private static final VarHandle PacketPlayOutBlockChange_b = unreflect(PacketPlayOutBlockChange.class, "b");
	private static final VarHandle PacketPlayOutMultiBlockChange_b = unreflect(PacketPlayOutMultiBlockChange.class, "b");
	private static final VarHandle PacketPlayOutMultiBlockChange_c = unreflect(PacketPlayOutMultiBlockChange.class, "c");
	private static final VarHandle PacketPlayOutMultiBlockChange_d = unreflect(PacketPlayOutMultiBlockChange.class, "d");

	private static final NamespacedKey fbk = new NamespacedKey("blocks", "block.overlay");
	private static final String fbv = "overlayed";

	public IllusionRegistryImpl(EventRegistry registry, OverlayLocationRegistry locationRegistry) {
		blockMap = new HashMap<>();
		this.overlays = locationRegistry;
		registry.subscribeAll(this);
	}

	@Override
	public void register(FakeBlock block) {
		getFakeBlocks().put(block.getLocatable().getBlockLocation(), block);
		if (block.getBuilder().getAspects().containsKey(AspectEnum.OVERLAYABLE.getName())) {
			block.getOverlay().registerLocation();
			FallingBlock fallingBlock = block.spawn();
			fallingBlock.getPersistentDataContainer().set(fbk, PersistentDataType.STRING, fbv);
			getOverlayRegistry().getFalling().put(block.getOverlay(), fallingBlock.getUniqueId());
		}
	}


	@Override
	public void unregister(FakeBlock block) {
		getFakeBlocks().remove(block.getLocatable().getBlockLocation());
		if (block.getBuilder().getAspects().containsKey(AspectEnum.OVERLAYABLE.getName())) {
			block.getOverlay().registerLocation();
			FallingBlock fallingBlock = block.spawn();
			fallingBlock.getPersistentDataContainer().remove(fbk);
			getOverlayRegistry().getFalling().remove(block.getOverlay());
		}
	}

	@Override
	public @Nullable FakeBlock getBlockAt(Location location) {
		return getFakeBlocks().get(location.getBlock().getLocation());
	}

	@Override
	public @Nullable Overlayable getOverlayAt(Location location) {
		return getOverlayRegistry().getOverlays().get(location.getBlock().getLocation());
	}

	@Override
	public @NotNull HashMap<Location, FakeBlock> getFakeBlocks() {
		return this.blockMap;
	}

	@Override
	public OverlayLocationRegistry getOverlayRegistry() {
		return this.overlays;
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


	@Subscribe
	void sendBlockChange(PlayerEventContext context, PacketPlayOutBlockChange packet) {
	//	Location  location = CraftLocation.toBukkit(packet.c(), context.getPlayer().getWorld());
		Vector vector = CraftVector.toBukkit(packet.c().b());
		Location location = vector.toLocation(context.getPlayer().getWorld());
		FakeBlock block    = getBlockAt(location);
		if (block == null) return;

		context.mutate(new PacketPlayOutBlockChange(packet.c(), ((CraftBlockData) block.getProjectedBlockData()).getState()));
	}

	@Subscribe
	@SuppressWarnings("PointlessBitwiseExpression")
	void sendMultiBlockChange(PlayerEventContext context, PacketPlayOutMultiBlockChange packet) {
		SectionPosition pos  = (SectionPosition) PacketPlayOutMultiBlockChange_b.get(packet);
		short[]         rel  = (short[]) PacketPlayOutMultiBlockChange_c.get(packet);
		IBlockData[]    data = (IBlockData[]) PacketPlayOutMultiBlockChange_d.get(packet);

		int cx = pos.u() << 4;
		int cz = pos.w() << 4;
		int cy = pos.v() << 4;

		for (int i = 0; i < rel.length; i++) {
			short pack = rel[i];

			byte rx = (byte) ((pack >>> 8) & 0xf);
			byte rz = (byte) ((pack >>> 4) & 0xf);
			byte ry = (byte) ((pack >>> 0) & 0xf);

			int x = rx + cx;
			int z = rz + cz;
			int y = ry + cy;

			FakeBlock block = getBlockAt(new Location(context.getPlayer().getWorld(), x, y, z));
			if (block == null) continue;

			data[i] = ((CraftBlockData) block.getProjectedBlockData()).getState();
		}
	}

	public void clickHighlight(PlayerEventContext context, PlayerInteractAtEntityEvent event) {
		if(event.getHand() != EquipmentSlot.HAND) return;
		Entity entity = event.getRightClicked();
		if (!(entity instanceof FallingBlock fb)) return;
		PersistentDataContainer pdc = fb.getPersistentDataContainer();
		if (!(pdc.has(fbk) && fbv.equals(pdc.get(fbk, PersistentDataType.STRING)))) return;
		Overlayable overlay = getOverlayAt(entity.getLocation());
		if (overlay == null) return;
		overlay.getPlayerConsumer().accept(event.getPlayer());
	}
}
