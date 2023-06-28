package blocks.impl.illusions;

import blocks.block.aspects.overlay.ClickCallback;
import blocks.block.illusions.BlockOverlay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
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

	public FallingBlockOverlay(Location location, BlockData data, ClickCallback onInteract) {
		this(location, null, data, onInteract);
	}

	public FallingBlockOverlay(Location location, ChatColor color, BlockData data, ClickCallback onInteract) {
		super(location);
		this.color      = color;
		this.data       = data.clone();
		this.onInteract = onInteract;
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
	public FallingBlock spawnNew() {
		Location location = getBlockLocation();
		location.add(.5, 0, .5);
		FallingBlock block = getBlock().getWorld().spawnFallingBlock(location, Material.GLASS.createBlockData());
		block.setVelocity(new Vector(0, 0, 0));
		block.setSilent(true);
		block.setInvulnerable(true);
		block.setGravity(false);
		block.setDropItem(false);
		block.shouldAutoExpire(false);

		if(isHighlighted()) {
			block.setGlowing(true);
			Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
			Team       team       = getScoreboardTeam(scoreboard, color.name());
			team.setColor(color);
			team.addEntity(block);
		}

		return block;
	}

	@Override
	public ClickCallback getCallback() {
		return onInteract;
	}

	private static Team getScoreboardTeam(Scoreboard scoreboard, String name) {
		Team team = scoreboard.getTeam(name);
		return team == null ? scoreboard.registerNewTeam(name) : team;
	}

}
