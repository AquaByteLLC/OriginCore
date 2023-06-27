package blocks.impl.aspect.illusion;

import blocks.BlocksAPI;
import blocks.block.aspects.illusions.FakeBlock;
import blocks.block.aspects.illusions.registry.IllusionRegistry;
import blocks.block.aspects.location.BlockLocatable;
import blocks.block.aspects.overlay.Overlayable;
import blocks.block.builder.OriginBlockBuilder;
import blocks.block.factory.AspectFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class IllusionBlock implements FakeBlock {

	private final OriginBlockBuilder builder;
	private BlockData fakeData;
	private BlockLocatable locatable;
	private Overlayable overlayable;
	private final IllusionRegistry registry = BlocksAPI.getIllusionRegistry();

	public IllusionBlock(OriginBlockBuilder builder, BlockLocatable locatable, Overlayable overlayable) {
		this.builder = builder;
		this.locatable = locatable;
		this.overlayable = overlayable;
	}

	public IllusionBlock(OriginBlockBuilder builder, AspectFactory factory) {
		this.builder = builder;
		this.locatable = factory.newLocatable();
		this.overlayable = factory.newOverlayable();
	}

	@Override
	public OriginBlockBuilder getBuilder() {
		return this.builder;
	}

	@Override
	public FakeBlock setProjectedBlockData(BlockData fakeData) {
		this.fakeData = fakeData;
		return this;
	}

	@Override
	public BlockData getProjectedBlockData() {
		return this.fakeData;
	}

	@Override
	public FallingBlock spawn() {
		Location location = this.locatable.getBlockLocation();
		location.add(.5, 0, .5);
		FallingBlock block = this.locatable.getBlock().getWorld().spawnFallingBlock(location, Material.GLASS.createBlockData());
		block.setVelocity(new Vector(0, 0, 0));
		block.setGlowing(true);
		block.setSilent(true);
		block.setInvulnerable(true);
		block.setGravity(false);
		block.setDropItem(false);
		block.shouldAutoExpire(false);
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team = getScoreboardTeam(scoreboard, this.overlayable.getOverlayColor().name());
		team.setColor(ChatColor.valueOf(this.overlayable.getOverlayColor().name()));
		team.addEntity(block);
		return block;
	}

	@Override
	public void despawn() {
		FallingBlock spawn = spawn();
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team = getScoreboardTeam(scoreboard, this.overlayable.getOverlayColor().name());
		team.removeEntity(spawn);
		spawn.remove();
	}

	@Override
	public Overlayable getOverlay() {
		return this.overlayable;
	}

	@Override
	public BlockLocatable getLocatable() {
		return this.locatable;
	}

	@Override
	public FakeBlock setOverlay(Overlayable overlay) {
		this.overlayable = overlay;
		return this;
	}

	@Override
	public FakeBlock setLocatable(BlockLocatable locatable) {
		this.locatable = locatable;
		return this;
	}

	@Override
	public IllusionRegistry getRegistry() {
		return this.registry;
	}

	private static Team getScoreboardTeam(Scoreboard scoreboard, String name) {
		Team team = scoreboard.getTeam(name);
		return team == null ? scoreboard.registerNewTeam(name) : team;
	}
}
