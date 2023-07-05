package blocks.impl.illusions.impl;

import blocks.block.illusions.BlockOverlay;
import blocks.block.util.ClickCallback;
import blocks.block.util.PacketReceiver;
import blocks.impl.illusions.BlockAdapter;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.entity.monster.EntityShulker;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftFallingBlock;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftShulker;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftSlime;
import org.bukkit.entity.Player;
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

	public FallingBlockOverlay(Location location, ChatColor color, BlockData data, ClickCallback onInteract) {
		super(location);
		this.color      = color;
		this.data       = data == null ? null : data.clone();
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
	public boolean hasOverlayData() {
		return data != null;
	}

	@Override
	public ClickCallback getCallback() {
		return onInteract;
	}

	@Override
	public void send(PacketReceiver receiver) {
		if(!hasEntity()) return;
		CraftEntity ent = getEntity();

		//https://www.spigotmc.org/threads/protocollib-and-fallingblock.373612/
		//https://web.archive.org/web/20230522171428/https://wiki.vg/Entity_metadata#Entity
		//https://wiki.vg/Object_Data#Falling_Block

		// EntityFallingBlock#S() creates a spawn packet, however the iblockdata is set
		// in a private constructor, available thru EntityFallingBlock.fall(...)
		// BUT... that method actually spawns the block in.
		// so it's easier just to set the packet data manually
//		ent.getHandle().S();
//		receiver.sendPackets(ent.getHandle().S());

		Location loc = getBlockLocation().add(.5, 0, .5);
		Entity var0 = ent.getHandle();
		receiver.sendPackets(new PacketPlayOutSpawnEntity(var0.af(), var0.cs(),
														  loc.getX(), loc.getY(), loc.getZ(),
														  0f, 0f, // yaw, pitch
														  var0.ae(),
														  hasOverlayData() ? Block.i(((CraftBlockData) data).getState()) : 0, // in the case of falling block
														  var0.dj(), 0f /* head rotation ? */),
							 new PacketPlayOutEntityMetadata(ent.getEntityId(), var0.aj().c()));
	}

	@Override
	public void remove(PacketReceiver receiver) {
		if(!hasEntity()) return;
		CraftEntity ent = getEntity();
		receiver.sendPackets(new PacketPlayOutEntityDestroy(ent.getEntityId()));
	}

	private CraftEntity entity;

	private CraftEntity getEntity() {
		if (entity == null)
			entity = spawnNew();
		return entity;
	}

	private CraftEntity spawnNew() {
		CraftEntity the = null;

		if (hasOverlayData()) { // falling block
			EntityFallingBlock entity = new EntityFallingBlock(EntityTypes.L, ((CraftWorld) getBlock().getWorld()).getHandle());
			CraftFallingBlock craft = new CraftFallingBlock(((CraftServer) Bukkit.getServer()), entity);
			craft.setDropItem(false);
			craft.shouldAutoExpire(false);
			the = craft;
		} else if (isHighlighted()) { // slime
			// shuklers do not work since their stupid little faces don't turn invisible
			// but medium slimes work just fine
			EntitySlime entity = new EntitySlime(EntityTypes.aL, ((CraftWorld) getBlock().getWorld()).getHandle());
			CraftSlime  craft  = new CraftSlime(((CraftServer) Bukkit.getServer()), entity);
			craft.setAI(false);
			craft.setSize(2);
			craft.setInvisible(true);
			craft.setCollidable(false);
			the = craft;
		}

		if(the != null) {
			the.setVelocity(new Vector(0, 0, 0));
			the.setSilent(true);
			the.setInvulnerable(true);
			the.setGravity(false);

			if (isHighlighted()) {
				the.setGlowing(true);
				Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
				Team       team       = getScoreboardTeam(scoreboard, color.name());
				team.setColor(color);
				team.addEntity(the);
			}
		}

		return the;
	}

	public int getEntityID() {
		return hasEntity() ? getEntity().getEntityId() : -1;
	}

	private boolean hasEntity() {
		return isHighlighted() || hasOverlayData();
	}

	private static Team getScoreboardTeam(Scoreboard scoreboard, String name) {
		Team team = scoreboard.getTeam(name);
		return team == null ? scoreboard.registerNewTeam(name) : team;
	}

}
