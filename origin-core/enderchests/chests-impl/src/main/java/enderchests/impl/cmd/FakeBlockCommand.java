package enderchests.impl.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import enderchests.block.FakeBlock;
import enderchests.block.FakeBlockFactory;
import enderchests.IllusionRegistry;
import enderchests.impl.block.FallingBlockHighlight;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

/**
 * @author vadim
 */
@CommandAlias("fake")
public class FakeBlockCommand extends BaseCommand {

	private final IllusionRegistry registry;
	private final FakeBlockFactory factory;

	public FakeBlockCommand(IllusionRegistry registry, FakeBlockFactory factory) {
		this.registry = registry;
		this.factory  = factory;
	}

	@Subcommand("set")
	void set(Player sender, Material material) {
		if(!material.isBlock()) {
			sender.sendMessage("not a block bruh");
			return;
		}

		Block block = sender.getTargetBlockExact(10);
		if(block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}
		FakeBlock fake = factory.newFakeBlock(block.getLocation(), material.createBlockData());

		registry.register(fake);
		sender.sendBlockChange(fake.getBlockLocation(), fake.getProjectedBlockData());
		sender.sendMessage("done");
	}

	@Subcommand("del")
	void del(Player sender) {
		Block block = sender.getTargetBlockExact(10);
		if(block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}

		FakeBlock fake = registry.getBlockAt(block.getLocation());
		if(fake == null) {
			sender.sendMessage("that's real (unlike you)");
			return;
		}

		registry.unregister(fake);
		sender.sendBlockChange(block.getLocation(), block.getBlockData());
		sender.sendMessage("done");
	}

	@Subcommand("highlight")
	void highlight(Player sender, ChatColor color) {
		Block block = sender.getTargetBlockExact(10);
		if(block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}

		Location location = block.getLocation();
		location.add(.5, 0, .5);
		Shulker outline = block.getWorld().spawn(location, Shulker.class);
		outline.setVelocity(new Vector(0, 0, 0));
		outline.setGravity(false);
		outline.setGlowing(true);
		outline.setAI(false);
		outline.setSilent(true);
		outline.setPeek(0f);
		outline.setInvulnerable(true);
		outline.setInvisible(true);

		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team = getScoreboardTeam(scoreboard, color.name());
		team.setColor(color);
		team.addEntity(outline);
		sender.sendMessage("done");
	}

	@Subcommand("highlight")
	void highlight(Player sender, Material material, ChatColor color) {
		Block block = sender.getTargetBlockExact(10);
		if(block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}

		Location location = block.getLocation();
		location.add(.5, 0, .5);
		FallingBlock outline = block.getWorld().spawnFallingBlock(location, material.createBlockData());
		outline.setVelocity(new Vector(0, 0, 0));
		outline.setGravity(false);
		outline.setGlowing(true);
		outline.setSilent(true);
		outline.setInvulnerable(true);

		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team = getScoreboardTeam(scoreboard, color.name());
		team.setColor(color);
		team.addEntity(outline);
		sender.sendMessage("done");
	}

	private static Team getScoreboardTeam(Scoreboard scoreboard, String name) {
		Team team = scoreboard.getTeam(name);
		return team == null ? scoreboard.registerNewTeam(name) : team;
	}


}
