package enderchests.impl.cmd;

import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionRegistry;
import blocks.block.illusions.IllusionsAPI;
import blocks.block.util.PlayerInteraction;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commons.util.BukkitUtil;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
@CommandAlias("illusion")
public class IllusionCommand extends BaseCommand {

	private final IllusionsAPI api;
	boolean global = true;

	public IllusionCommand(IllusionsAPI api) {
		this.api = api;
	}

	private IllusionRegistry registry(Player player) {
		if(global)
			return api.globalRegistry();
		else
			return api.localRegistry(player);
	}

	@Subcommand("querymode")
	void pero(Player sender) {
		sender.sendMessage(global ? "in global illusion mode" : "in local player mode");
	}

	@Subcommand("setlocalmode")
	void loco(Player sender) {
		global = false;
		sender.sendMessage("Now in local player mode");
	}

	@Subcommand("setglobalmode")
	void contigo(Player sender) {
		global = true;
		sender.sendMessage("Now in global illusion mode");
	}

	@Subcommand("fakeblock")
	void set(Player sender, Material material, @Optional BlockFace face) {
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

		if(data instanceof Directional directional && face != null)
			directional.setFacing(face);

		FakeBlock fake = api.newIllusionBuilder().fakeProjectedBlockData(data).build(block.getLocation());

		registry(sender).register(fake);
		sender.sendBlockChange(fake.getBlockLocation(), fake.getProjectedBlockData());
		sender.sendMessage("done");
	}

	private static void chestToggle(Player player, PlayerInteraction click, Location location) {
		PacketPlayOutBlockAction action = new PacketPlayOutBlockAction(CraftLocation.toBlockPosition(location),
																	   ((CraftBlockData) Material.ENDER_CHEST.createBlockData()).getState().b(),
																	   1, click == PlayerInteraction.RIGHT_CLICK ? 1 : 0);
		BukkitUtil.sendPacket(player, action);
	}

	@Subcommand("highlight")
	void highlight(Player sender, ChatColor color) {
		Block block = sender.getTargetBlockExact(10);
		if (block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}

		Location location = block.getLocation();
		FakeBlock fake = api.newIllusionBuilder()
							.overlayHighlightColor(color)
							.overlayClickCallback((Player player, PlayerInteraction click) -> chestToggle(player, click, location))
							.build(location);
		registry(sender).register(fake);
	}

	@Subcommand("overlay")
	void overlay(Player sender, Material material) {
		Block block = sender.getTargetBlockExact(10);
		if(block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}

		Location location = block.getLocation();
		FakeBlock fake = api.newIllusionBuilder()
							.overlayData(material.createBlockData())
							.overlayClickCallback((Player player, PlayerInteraction click) -> chestToggle(player, click, location))
							.build(location);
		registry(sender).register(fake);
	}

	@Subcommand("reset")
	void del(Player sender) {
		Block block = sender.getTargetBlockExact(10);
		if(block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}

		IllusionRegistry registry = registry(sender);
		Location location = block.getLocation();
		FakeBlock fake = registry.getBlockAt(location);
		if(fake == null) {
			sender.sendMessage("dat thing is real");
			return;
		}
		registry.unregister(fake);
		sender.sendMessage("done");
	}

}