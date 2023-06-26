package enderchests.impl.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import commons.BukkitUtil;
import enderchests.block.BlockHighlight;
import enderchests.block.FakeBlock;
import enderchests.block.BlockFactory;
import enderchests.IllusionRegistry;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntityChest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

/**
 * @author vadim
 */
@CommandAlias("illusion")
public class IllusionCommand extends BaseCommand {

	private final IllusionRegistry registry;
	private final BlockFactory     factory;

	public IllusionCommand(IllusionRegistry registry, BlockFactory factory) {
		this.registry = registry;
		this.factory  = factory;
	}

	@Subcommand("fakeblock set")
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

	@Subcommand("fakeblock reset")
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

		Location       location = block.getLocation();
		BlockHighlight highlight = factory.newBlockHighlight(location, color, (player) -> {
			player.sendMessage("clicked!");
		// 1, 1

			PacketPlayOutBlockAction action = new PacketPlayOutBlockAction(CraftLocation.toBlockPosition(location),
																		   ((CraftBlockData) Material.ENDER_CHEST.createBlockData()).getState().b(),
																		   1, 1);

			BukkitUtil.sendPacket(player, action);
		});
		registry.register(highlight);
	}

	@Subcommand("unhighlight")
	void unhighlight(Player sender) {
		Block block = sender.getTargetBlockExact(10);
		if(block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}

		BlockHighlight highlight = registry.getHighlightAt(block.getLocation());
		if(highlight == null) {
			sender.sendMessage("buh");
			return;
		}

		registry.unregister(highlight);
	}

}
