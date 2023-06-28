package enderchests.impl.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import commons.BukkitUtil;
import enderchests.block.BlockOverlay;
import enderchests.block.FakeBlock;
import enderchests.block.BlockFactory;
import enderchests.IllusionRegistry;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.entity.Player;

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
		set(sender, material, BlockFace.EAST);
	}

	@Subcommand("fakeblock set")
	void set(Player sender, Material material, BlockFace face) {
		if(!material.isBlock()) {
			sender.sendMessage("not a block bruh");
			return;
		}

		Block block = sender.getTargetBlockExact(10);
		if(block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}
		BlockData data = material.createBlockData();

		if(data instanceof Directional directional)
			directional.setFacing(face);

		FakeBlock fake = factory.newFakeBlock(block.getLocation(), data);

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
		BlockOverlay highlight = factory.newOverlay(location, Material.GLASS.createBlockData(), color, (player) -> {
			player.sendMessage("clicked!");
			// 1, 1

			PacketPlayOutBlockAction action = new PacketPlayOutBlockAction(CraftLocation.toBlockPosition(location),
																		   ((CraftBlockData) Material.ENDER_CHEST.createBlockData()).getState().b(),
																		   1, 1);

			BukkitUtil.sendPacket(player, action);
		});
		registry.register(highlight);
	}

	@Subcommand("highlight")
	void highlight(Player sender, Material material) {
		Block block = sender.getTargetBlockExact(10);
		if(block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}

		Location       location = block.getLocation();
		BlockOverlay highlight = factory.newOverlay(location, material.createBlockData(), null, (player) -> {
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

		BlockOverlay highlight = registry.getHighlightAt(block.getLocation());
		if(highlight == null) {
			sender.sendMessage("buh");
			return;
		}

		registry.unregister(highlight);
	}

}
