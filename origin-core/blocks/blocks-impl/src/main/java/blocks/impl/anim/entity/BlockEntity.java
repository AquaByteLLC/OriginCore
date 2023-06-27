package blocks.impl.anim.entity;

import com.mojang.datafixers.util.Pair;
import commons.entity.EntityHelper;
import commons.events.impl.packet.PacketEventListener;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.List;

public class BlockEntity extends EntityHelper.EntityWrapper<EntityArmorStand> {

	private static final int MOVEMENT_TICKS = 5;

	private final Player owner;
	private final Block block;
	private final ItemStack item;

	private double difX = 0;
	private double difY = 0;
	private double difZ = 0;

	private static final VarHandle aM;

	static {
		Field field;
		VarHandle handle;
		try {
			field = Entity.class.getDeclaredField("am");
			field.setAccessible(true);
			handle = MethodHandles.privateLookupIn(Entity.class, MethodHandles.lookup()).unreflectVarHandle(field);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		aM = handle;
	}

	public BlockEntity(Player owner, World world, Block block, ItemStack item) {
		super(
				new EntityArmorStand(world, block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5),
				"block-entity",
				true,
				1,
				1
		);
		this.owner = owner;
		this.block = block;
		this.item = item;
	}

	@Override
	public void tick() {
		System.out.println("4");
		if (this.getTicksRan() == 0) {
			System.out.println("5");
			this.spawnEntity(this.owner);

			DataWatcher watcher = ((DataWatcher) aM.get((getEntity()).getBukkitEntity().getHandle()));
			final PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(this.getEntity().getBukkitEntity().getEntityId(),
					List.of(Pair.of(EnumItemSlot.f, CraftItemStack.asNMSCopy(this.item))));
			final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(
					this.getEntity().getBukkitEntity().getEntityId(), watcher.c());

			PacketEventListener.sendPacket(this.owner, packetPlayOutEntityEquipment);
			PacketEventListener.sendPacket(this.owner, packetPlayOutEntityMetadata);
		}

		System.out.println("6");
		short moveX = 0;
		short moveY = -2;
		short moveZ = 0;

		this.getEntity().e(this.getEntity().dy() + (float) (Math.PI*3));
		final byte headRotation = (byte) (this.getEntity().dy() * 256.0F / 360.0F);

		if (this.getTicksRan() == MOVEMENT_TICKS) {
			final Location playerLocation = this.owner.getLocation();
			this.difX = playerLocation.getX() - (block.getX() + 0.5);
			this.difY = playerLocation.getY() - (block.getY() + 0.5);
			this.difZ = playerLocation.getZ() - (block.getZ() + 0.5);
		}

		if (this.getTicksRan() > MOVEMENT_TICKS) {
			final double prevX = this.getEntity().getBukkitEntity().getLocation().getX();
			final double prevY = this.getEntity().getBukkitEntity().getLocation().getY();
			final double prevZ = this.getEntity().getBukkitEntity().getLocation().getZ();
			final double curX = prevX + this.difX / 3;
			final double curY = prevY + this.difY / 3;
			final double curZ = prevZ + this.difZ / 3;
			this.getEntity().a(curX, curY, curZ);

			moveX = (short) ((curX * 32 - prevX * 32) * 128);
			moveY = (short) ((curY * 32 - prevY * 32) * 128);
			moveZ = (short) ((curZ * 32 - prevZ * 32) * 128);

			if (this.getTicksRan() > 10) {
				this.cancel();

				final PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(this.getEntity().getBukkitEntity().getEntityId());
				PacketEventListener.sendPacket(this.owner, packetPlayOutEntityDestroy);
				return;
			}
		}

		final PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packetPlayOutRelEntityMoveLook = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
				this.getEntity().getBukkitEntity().getEntityId(),
				moveX, moveY, moveZ,
				headRotation, (byte) 0,
				true);
		PacketEventListener.sendPacket(this.owner, packetPlayOutRelEntityMoveLook);
	}

	@Override
	protected void initEntity() {
		final EntityArmorStand entityArmorStand = this.getEntity();
		entityArmorStand.j(true);
		entityArmorStand.t(true);
	}
}

