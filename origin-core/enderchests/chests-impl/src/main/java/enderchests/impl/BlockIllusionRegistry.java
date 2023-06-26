package enderchests.impl;

import commons.events.api.PlayerEventContext;
import commons.events.api.Subscribe;
import enderchests.block.BlockHighlight;
import enderchests.block.FakeBlock;
import enderchests.impl.block.FallingBlockHighlight;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutMultiBlockChange;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author vadim
 */
public class BlockIllusionRegistry implements enderchests.IllusionRegistry {

	//todo: location storage in other registries is fucked
	private final Map<Location, FakeBlock>      fake       = new HashMap<>();
	private final Map<Location, BlockHighlight> highlights = new HashMap<>();
	private final Map<BlockHighlight, UUID>     falling    = new HashMap<>();

	@Override
	public void register(FakeBlock block) {
		fake.put(block.getBlockLocation(), block);
	}

	@Override
	public void unregister(FakeBlock block) {
		fake.remove(block.getBlockLocation());
	}

	private static final NamespacedKey fbk = new NamespacedKey("blocks", "block.highlight");
	private static final String fbv = "highlighted";

	@Override
	public void register(BlockHighlight highlight) {
		highlights.put(highlight.getBlockLocation(), highlight);

		FallingBlockHighlight impl = ((FallingBlockHighlight) highlight);

		FallingBlock block = impl.spawn();
		block.getPersistentDataContainer().set(fbk, PersistentDataType.STRING, fbv);
		falling.put(highlight, block.getUniqueId());
	}

	@Override
	public void unregister(BlockHighlight highlight) {
		highlights.remove(highlight.getBlockLocation());

		UUID fuid = falling.remove(highlight);
		if (fuid != null) {
			Entity entity = highlight.getBlock().getWorld().getEntity(fuid);

			if (entity instanceof FallingBlock block)
				block.remove();
		}
	}

	@Override
	public @Nullable FakeBlock getBlockAt(Location location) {
		return fake.get(location.getBlock().getLocation());
	}

	@Override
	public @Nullable BlockHighlight getHighlightAt(Location location) {
		return highlights.get(location.getBlock().getLocation());
	}

	private static final VarHandle PacketPlayOutBlockChange_b = unreflect(PacketPlayOutBlockChange.class, "b");

	private static final VarHandle PacketPlayOutMultiBlockChange_b = unreflect(PacketPlayOutMultiBlockChange.class, "b");
	private static final VarHandle PacketPlayOutMultiBlockChange_c = unreflect(PacketPlayOutMultiBlockChange.class, "c");
	private static final VarHandle PacketPlayOutMultiBlockChange_d = unreflect(PacketPlayOutMultiBlockChange.class, "d");

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
		Location  location = CraftLocation.toBukkit(packet.c(), context.getPlayer().getWorld());
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

	@Subscribe
	void clickHighlight(PlayerInteractAtEntityEvent event) {
		if(event.getHand() != EquipmentSlot.HAND) return;
		Entity entity = event.getRightClicked();

		if (!(entity instanceof FallingBlock block)) return;

		PersistentDataContainer pdc = block.getPersistentDataContainer();
		if (!(pdc.has(fbk) && fbv.equals(pdc.get(fbk, PersistentDataType.STRING)))) return;

		BlockHighlight highlight = getHighlightAt(entity.getLocation());
		if (highlight == null) return;

		FallingBlockHighlight impl = (FallingBlockHighlight) highlight;
		impl.onInteract.accept(event.getPlayer());
	}

}
