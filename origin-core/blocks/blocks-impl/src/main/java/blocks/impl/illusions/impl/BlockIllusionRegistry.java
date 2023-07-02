package blocks.impl.illusions.impl;

import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionRegistry;
import blocks.block.util.ClickCallback;
import blocks.block.util.PacketReceiver;
import blocks.block.util.PlayerInteraction;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import commons.util.ReflectUtil;
import commons.util.reflect.FieldAccess;
import commons.util.reflect.Reflection;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutMultiBlockChange;
import net.minecraft.world.EnumHand;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftVector;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

class BlockIllusionRegistry implements IllusionRegistry {

	private final JavaPlugin plugin;
	private final HashMap<Location, FakeBlock> byLocation = new HashMap<>();
	private final HashMap<Integer, FakeBlock> byOverlay = new HashMap<>();
	private final PacketReceiver receiver;

	BlockIllusionRegistry(JavaPlugin plugin, PacketReceiver receiver, EventRegistry events) {
		this.plugin = plugin;
		this.receiver = receiver;
		events.subscribeAll(this);
	}

	@Override
	public void register(FakeBlock block) {
		FakeBlock old = getBlockAt(block.getBlockLocation());
		if(old != null)
			unregister(old);

		byLocation.put(block.getBlockLocation(), block);
		block.send(receiver);
		if(block.hasOverlay())
			if(block instanceof PacketBasedFakeBlock packet)
				byOverlay.put(packet.getOverlay().getEntityID(), block);
	}

	@Override
	public void unregister(FakeBlock block) {
		byLocation.remove(block.getBlockLocation());
		block.remove(receiver);
		if(block.hasOverlay())
			if(block instanceof PacketBasedFakeBlock packet)
				byOverlay.remove(packet.getOverlay().getEntityID());
	}

	@Override
	public @Nullable FakeBlock getBlockAt(Location location) {
		return byLocation.get(location.getBlock().getLocation());
	}

	public @Nullable FakeBlock getBlockByEntityID(Integer id) {
		return byOverlay.get(id);
	}

	private static final FieldAccess<IBlockData> PacketPlayOutBlockChange_b = Reflection.unreflectFieldAccess(PacketPlayOutBlockChange.class, "b");

	@Subscribe
	void sendBlockChange(EventContext context, PacketPlayOutBlockChange packet) {
		if(!receiver.appliesTo(context.getPlayer())) return;
//		Location  location = CraftLocation.toBukkit(packet.c(), context.getPlayer().getWorld());
		Vector    vector   = CraftVector.toBukkit(packet.c().b()); // whatever
		Location  location = vector.toLocation(context.getPlayer().getWorld());
		FakeBlock block    = getBlockAt(location);
		if (block == null) return;
		if (!block.hasProjection()) return;

		PacketPlayOutBlockChange_b.set(packet, ((CraftBlockData) block.getProjectedBlockData()).getState());
	}

	private static final FieldAccess<SectionPosition> PacketPlayOutMultiBlockChange_b = Reflection.unreflectFieldAccess(PacketPlayOutMultiBlockChange.class, "b");
	private static final FieldAccess<short[]> PacketPlayOutMultiBlockChange_c = Reflection.unreflectFieldAccess(PacketPlayOutMultiBlockChange.class, "c");
	private static final FieldAccess<IBlockData[]> PacketPlayOutMultiBlockChange_d = Reflection.unreflectFieldAccess(PacketPlayOutMultiBlockChange.class, "d");

	@Subscribe
	@SuppressWarnings("PointlessBitwiseExpression")
	void sendMultiBlockChange(EventContext context, PacketPlayOutMultiBlockChange packet) {
		if(!receiver.appliesTo(context.getPlayer())) return;
		SectionPosition pos  = PacketPlayOutMultiBlockChange_b.get(packet);
		short[]         rel  = PacketPlayOutMultiBlockChange_c.get(packet);
		IBlockData[]    data = PacketPlayOutMultiBlockChange_d.get(packet);

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
			if (!block.hasProjection()) continue;

			data[i] = ((CraftBlockData) block.getProjectedBlockData()).getState();
		}
	}

	private static final FieldAccess<Integer> PacketPlayInUseEntity_a = Reflection.unreflectFieldAccess(PacketPlayInUseEntity.class, "a");

	@Subscribe
	void click(EventContext context, PacketPlayInUseEntity packet) throws Throwable {
		if (!receiver.appliesTo(context.getPlayer())) return;
		Player player = context.getPlayer();

		class adapter implements PacketPlayInUseEntity.c {

			ClickCallback callback;

			adapter(int entity) {
				FakeBlock fake = getBlockByEntityID(entity);

				if (fake == null) return;
				if (!fake.hasOverlay()) return;

				callback = fake.getOverlay().getCallback();
				context.setCancelled(true);
			}

			/**
			 * INTERACT
			 */
			@Override
			public void a(EnumHand enumHand) {
				if (enumHand != EnumHand.a) return; // MAIN_HAND
				if (callback != null)
					callback.onClick(player, PlayerInteraction.RIGHT_CLICK);
			}

			/**
			 * INTERACT_AT
			 */
			@Override
			public void a(EnumHand enumHand, Vec3D vec3D) {
				// this appears to fire alongside INTERACT
			}

			/**
			 * ATTACK
			 */
			@Override
			public void a() {
				if (callback != null)
					callback.onClick(player, PlayerInteraction.LEFT_CLICK);
			}

		}

		try {
			packet.a(new adapter(PacketPlayInUseEntity_a.get(packet)));
		} catch (Exception e) {
			ReflectUtil.serr("Caught exception in ClickCallback!");
			ReflectUtil.serr(e);
		}
	}
}
