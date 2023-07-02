package blocks.impl.illusions.impl;

import blocks.block.illusions.BlockOverlay;
import blocks.block.util.ClickCallback;
import blocks.impl.illusions.BlockAdapter;
import blocks.block.util.PacketReceiver;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftFallingBlock;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/**
 * @author vadim
 */
public class FallingBlockOverlay extends BlockAdapter implements BlockOverlay {

	private final ChatColor color;
	private final BlockData data;
	public final ClickCallback onInteract;

	@Deprecated(forRemoval = true)
	public FallingBlockOverlay(Location location, BlockData data, ClickCallback onInteract) {
		this(location, null, data, onInteract);
	}

	public FallingBlockOverlay(Location location, ChatColor color, BlockData data, ClickCallback onInteract) {
		super(location);
		this.color      = color;
		this.data       = data.clone();
		this.onInteract = onInteract == null ? (__, ___) -> { } : onInteract;
	}

	@Override
	public @Nullable ChatColor getHighlightColor() {
		return color;
	}

	@Override
	public boolean isHighlighted() {
		return color != null;
	}

	@Override
	public BlockData getOverlayData() {
		return data.clone();
	}

	@Override
	public ClickCallback getCallback() {
		return onInteract;
	}

	@Override
	public void send(PacketReceiver receiver) {
		CraftFallingBlock ent = getEntity();

		//https://www.spigotmc.org/threads/protocollib-and-fallingblock.373612/
		//https://web.archive.org/web/20230522171428/https://wiki.vg/Entity_metadata#Entity
		//https://wiki.vg/Object_Data#Falling_Block

		// EntityFallingBlock#S() creates a spawn packet, however the iblockdata is set
		// in a private constructor, available thru EntityFallingBlock.fall(...)
		// BUT... that method actually spawns the block in.
		// so it's easier just to set the packet data manually
//		ent.getHandle().S();

//		receiver.sendPackets(ent.getHandle().S());
		EntityFallingBlock var0 = ent.getHandle();
		receiver.sendPackets(new PacketPlayOutSpawnEntity(var0.af(), var0.cs(), var0.I, var0.J, var0.K, var0.dy(), var0.dw(), var0.ae(), Block.i(((CraftBlockData) data).getState()), var0.dj(), var0.ck()),
							 new PacketPlayOutEntityMetadata(ent.getEntityId(), var0.aj().c()));
	}

	@Override
	public void remove(PacketReceiver receiver) {
		CraftFallingBlock ent = getEntity();
		receiver.sendPackets(new PacketPlayOutEntityDestroy(ent.getEntityId()));
	}

	private CraftFallingBlock entityBlock;

	private CraftFallingBlock getEntity() {
		if (entityBlock == null)
			entityBlock = spawnNew();
		return entityBlock;
	}

	private CraftFallingBlock spawnNew() {
		Location location = getBlockLocation();
		location.add(.5, 0, .5);
		EntityFallingBlock entity = new EntityFallingBlock(EntityTypes.L, ((CraftWorld) getBlock().getWorld()).getHandle());
		entity.I = location.getX();
		entity.J = location.getY();
		entity.K = location.getZ();
		CraftFallingBlock craft = new CraftFallingBlock(((CraftServer) Bukkit.getServer()), entity);
		craft.setVelocity(new Vector(0, 0, 0));
		craft.setSilent(true);
		craft.setInvulnerable(true);
		craft.setGravity(false);
		craft.setDropItem(false);
		craft.shouldAutoExpire(false);

		if (isHighlighted()) {
			craft.setGlowing(true);
			Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
			Team       team       = getScoreboardTeam(scoreboard, color.name());
			team.setColor(color);
			team.addEntity(craft);
		}

		return craft;
	}

	public int getEntityID() {
		return getEntity().getEntityId();
	}

	private static Team getScoreboardTeam(Scoreboard scoreboard, String name) {
		Team team = scoreboard.getTeam(name);
		return team == null ? scoreboard.registerNewTeam(name) : team;
	}

}
