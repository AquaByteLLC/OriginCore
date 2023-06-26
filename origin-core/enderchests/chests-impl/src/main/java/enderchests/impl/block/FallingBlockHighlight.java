package enderchests.impl.block;

import enderchests.block.BlockHighlight;
import org.bukkit.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

/**
 * @author vadim
 */
public class FallingBlockHighlight extends BlockAdapter implements BlockHighlight {

	private final ChatColor color;
	public final Consumer<Player> onInteract;

	public FallingBlockHighlight(Location location, ChatColor color, Consumer<Player> onInteract) {
		super(location);
		this.color = color;
		this.onInteract = onInteract;
	}

	@Override
	public ChatColor getHighlightColor() {
		return color;
	}

	public FallingBlock spawn() {
		Location location = getBlockLocation();
		location.add(.5, 0, .5);
		FallingBlock block = getBlock().getWorld().spawnFallingBlock(location, Material.GLASS.createBlockData());
		block.setVelocity(new Vector(0, 0, 0));
		block.setGlowing(true);
		block.setSilent(true);
		block.setInvulnerable(true);
		block.setGravity(false);
		block.setDropItem(false);
		block.shouldAutoExpire(false);

		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team       team       = getScoreboardTeam(scoreboard, color.name());
		team.setColor(color);
		team.addEntity(block);

		return block;
	}

	private static Team getScoreboardTeam(Scoreboard scoreboard, String name) {
		Team team = scoreboard.getTeam(name);
		return team == null ? scoreboard.registerNewTeam(name) : team;
	}

}
