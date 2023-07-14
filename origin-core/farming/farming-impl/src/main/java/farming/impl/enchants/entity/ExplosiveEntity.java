package farming.impl.enchants.entity;

import commons.entity.EntityHelper;
import commons.util.BukkitUtil;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.level.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

public class ExplosiveEntity extends EntityHelper.EntityWrapper<EntityTNTPrimed> {

	private static final int TICK_RUNTIME = 5;
	private final Player owner;
	private final Block block;
	private final Consumer<Player> blockConsumer;

	public ExplosiveEntity(World world, Player player, Block block, Consumer<Player> blockConsumer) {
		super(new EntityTNTPrimed(
						world, block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5, ((CraftPlayer) player).getHandle()),
				"custom-tnt", true, 1, 1);
		this.owner = player;
		this.block = block;
		this.blockConsumer = blockConsumer;
	}

	@Override
	protected void initEntity() {
		this.getEntity().b(120);
	}

	@Override
	public void tick() {

		if (this.getTicksRan() == 0) {
			this.spawnEntity(this.owner);
		}

		if (this.getTicksRan() >= this.getEntity().j() - 40) {
			this.cancel();
			blockConsumer.accept(this.owner);
			final PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(this.getEntity().getBukkitEntity().getEntityId());
			BukkitUtil.sendPacket(this.owner, packetPlayOutEntityDestroy);
		}
	}
}
