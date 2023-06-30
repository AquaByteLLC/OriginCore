package blocks.impl.illusions;

import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionRegistry;
import blocks.block.util.PlayerInteraction;
import commons.events.api.EventContext;
import commons.events.api.EventRegistry;
import commons.events.api.Subscribe;
import commons.util.reflect.FieldAccess;
import commons.util.reflect.Reflection;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class BlockIllusionRegistry implements IllusionRegistry {

	private final HashMap<Location, FakeBlock> blocks = new HashMap<>();
	private final HashMap<FakeBlock, UUID> falling = new HashMap<>();
	private static final FieldAccess<IBlockData> PacketPlayOutBlockChange_b = Reflection.unreflectFieldAccess(PacketPlayOutBlockChange.class, "b");
	private static final FieldAccess<SectionPosition> PacketPlayOutMultiBlockChange_b = Reflection.unreflectFieldAccess(PacketPlayOutMultiBlockChange.class, "b");
	private static final FieldAccess<short[]> PacketPlayOutMultiBlockChange_c = Reflection.unreflectFieldAccess(PacketPlayOutMultiBlockChange.class, "c");
	private static final FieldAccess<IBlockData[]> PacketPlayOutMultiBlockChange_d = Reflection.unreflectFieldAccess(PacketPlayOutMultiBlockChange.class, "d");

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

	@Subscribe
	void sendBlockChange(EventContext context, PacketPlayOutBlockChange packet) {
//		Location  location = CraftLocation.toBukkit(packet.c(), context.getPlayer().getWorld());
		Vector    vector   = CraftVector.toBukkit(packet.c().b()); // whatever
		Location  location = vector.toLocation(context.getPlayer().getWorld());
		FakeBlock block    = getBlockAt(location);
		if (block == null) return;
		if (!block.hasProjection()) return;

		PacketPlayOutBlockChange_b.set(packet, ((CraftBlockData) block.getProjectedBlockData()).getState());
	}

	@Subscribe
	@SuppressWarnings("PointlessBitwiseExpression")
	void sendMultiBlockChange(EventContext context, PacketPlayOutMultiBlockChange packet) {
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

	@Subscribe
	public void left(EventContext context, PacketPlayInUseEntity event) {
		System.out.println(event.a());
//		Player player = event.getPlayer();
//		if(event.getHand() != EquipmentSlot.HAND) return;
//
//		Entity clicked = player.getTargetEntity(5);
//		if(clicked == null) return;
//
//		processClick(player, clicked, PlayerInteraction.LEFT_CLICK);
	}

	@Subscribe
	public void right(EventContext context, PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		if (event.getHand() != EquipmentSlot.HAND) return;

		processClick(player, event.getRightClicked(), PlayerInteraction.RIGHT_CLICK);
	}

	private void processClick(Player player, Entity entity, PlayerInteraction interaction) {
		if (!(entity instanceof FallingBlock fb)) return;

		PersistentDataContainer pdc = fb.getPersistentDataContainer();
		if (!(pdc.has(fbk) && fbv.equals(pdc.get(fbk, PersistentDataType.STRING)))) return;

		FakeBlock fake = getBlockAt(entity.getLocation());
		if (fake == null) return;
		if (!fake.hasOverlay()) return;
		fake.getOverlay().getCallback().onClick(player, interaction);
	}

}
