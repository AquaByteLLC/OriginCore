package blocks.impl.anim.entity;

import com.mojang.datafixers.util.Pair;
import commons.entity.EntityHelper;
import commons.util.BukkitUtil;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockEntity extends EntityHelper.EntityWrapper<EntityArmorStand> {

	private static final int MOVEMENT_TICKS = 5;

	private final Player owner;
	private final Block block;
	private final ItemStack item;

	private double difX = 0;
	private double difY = 0;
	private double difZ = 0;
	private final boolean dropItem;

	public BlockEntity(Player owner, World world, Block block, ItemStack item, boolean dropItem) {
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
		this.dropItem = dropItem;
	}

	@Override
	public void tick() {
		if (this.getTicksRan() == 0) {
			this.spawnEntity(this.owner);

			DataWatcher watcher = getEntity().aj();
			final PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(this.getEntity().getBukkitEntity().getEntityId(),
					List.of(Pair.of(EnumItemSlot.f, CraftItemStack.asNMSCopy(this.item))));
			final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(
					this.getEntity().getBukkitEntity().getEntityId(), watcher.c());

			BukkitUtil.sendPacket(this.owner, packetPlayOutEntityEquipment);
			BukkitUtil.sendPacket(this.owner, packetPlayOutEntityMetadata);
		}

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
				BukkitUtil.sendPacket(this.owner, packetPlayOutEntityDestroy);
				if (dropItem) owner.getInventory().addItem(item);
				return;
			}
		}

		final PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packetPlayOutRelEntityMoveLook = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
				this.getEntity().getBukkitEntity().getEntityId(),
				moveX, moveY, moveZ,
				headRotation, (byte) 0,
				true);
		BukkitUtil.sendPacket(this.owner, packetPlayOutRelEntityMoveLook);
	}

	@Override
	protected void initEntity() {
		final EntityArmorStand entityArmorStand = this.getEntity();
		entityArmorStand.j(true);
		entityArmorStand.t(true);
	}
}

