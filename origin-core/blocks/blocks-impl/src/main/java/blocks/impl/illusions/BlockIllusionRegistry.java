package blocks.impl.illusions;

import blocks.block.util.PlayerInteraction;
import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionRegistry;
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
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

public class BlockIllusionRegistry implements IllusionRegistry {

	private final HashMap<Location, FakeBlock> blocks = new HashMap<>();
	private final HashMap<FakeBlock, UUID> falling = new HashMap<>();
	private static final VarHandle PacketPlayOutBlockChange_b = unreflect(PacketPlayOutBlockChange.class, "b");
	private static final VarHandle PacketPlayOutMultiBlockChange_b = unreflect(PacketPlayOutMultiBlockChange.class, "b");
	private static final VarHandle PacketPlayOutMultiBlockChange_c = unreflect(PacketPlayOutMultiBlockChange.class, "c");
	private static final VarHandle PacketPlayOutMultiBlockChange_d = unreflect(PacketPlayOutMultiBlockChange.class, "d");

	private static final NamespacedKey fbk = new NamespacedKey("blocks", "block.overlay");
	private static final String fbv = "overlayed";

	public BlockIllusionRegistry(EventRegistry registry) {
		registry.subscribeAll(this);
	}

	@Override
	public void register(FakeBlock block) {
		blocks.put(block.getBlockLocation(), block);
		if (block.hasOverlay()) {
			unoverlay(block);
			FallingBlock fallingBlock = block.getOverlay().spawnNew();
			fallingBlock.getPersistentDataContainer().set(fbk, PersistentDataType.STRING, fbv);
			falling.put(block, fallingBlock.getUniqueId());
		}
	}

	@Override
	public void unregister(FakeBlock block) {
		blocks.remove(block.getBlockLocation());
		unoverlay(block);
	}

	private void unoverlay(FakeBlock block) {
		UUID fuid = falling.remove(block);
		if (fuid != null) {
			Entity entity = block.getBlock().getWorld().getEntity(fuid);

			if (entity instanceof FallingBlock fb)
				fb.remove();
		}
	}

	@Override
	public @Nullable FakeBlock getBlockAt(Location location) {
		return blocks.get(location.getBlock().getLocation());
	}

	@Override
	public boolean isOverlayEntity(FallingBlock block) {
		return block != null && block.getPersistentDataContainer().has(fbk) && fbv.equals(block.getPersistentDataContainer().get(fbk, PersistentDataType.STRING));
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
//		Location  location = CraftLocation.toBukkit(packet.c(), context.getPlayer().getWorld());
		Vector vector = CraftVector.toBukkit(packet.c().b()); // whatever
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

	@Subscribe
	public void left(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if(!(damager instanceof Player player)) return;
		processClick(player, event.getEntity(), PlayerInteraction.LEFT_CLICK);
	}

	@Subscribe
	public void right(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		if(event.getHand() != EquipmentSlot.HAND) return;

		processClick(player, event.getRightClicked(), PlayerInteraction.RIGHT_CLICK);
	}

	private void processClick(Player player, Entity entity, PlayerInteraction interaction) {
		if (!(entity instanceof FallingBlock fb)) return;

		PersistentDataContainer pdc = fb.getPersistentDataContainer();
		if (!(pdc.has(fbk) && fbv.equals(pdc.get(fbk, PersistentDataType.STRING)))) return;

		FakeBlock fake = getBlockAt(entity.getLocation());
		if (fake == null) return;
		if(!fake.hasOverlay()) return;
		fake.getOverlay().getCallback().onClick(player, interaction);
	}

}
