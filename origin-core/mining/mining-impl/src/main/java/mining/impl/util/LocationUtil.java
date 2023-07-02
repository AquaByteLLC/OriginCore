package mining.impl.util;

import blocks.BlocksAPI;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.builder.FixedAspectHolder;
import blocks.impl.BlocksPlugin;
import blocks.impl.account.BlockAccount;
import net.minecraft.core.BlockPosition;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LocationUtil {
	public static List<FixedAspectHolder> fracture(Player player, Block block, BlockPosition position) {
		List<FixedAspectHolder> blocks = new ArrayList<>();

		BlockAccount blockAccount = BlocksPlugin.get().getInstance(BlocksPlugin.class).getAccountStorage().getAccount(player);
		RegenerationRegistry regenerationRegistry = blockAccount.getRegenerationRegistry();

		Location blockEast = block.getRelative(BlockFace.EAST).getLocation();
		Location blockWest = block.getRelative(BlockFace.WEST).getLocation();
		Location blockNorth = block.getRelative(BlockFace.NORTH).getLocation();
		Location blockSouth = block.getRelative(BlockFace.SOUTH).getLocation();

		BlockPosition positionEast = new BlockPosition(blockEast.getBlockX(), blockEast.getBlockY(), blockEast.getBlockZ());
		BlockPosition positionWest = new BlockPosition(blockWest.getBlockX(), blockWest.getBlockY(), blockWest.getBlockZ());
		BlockPosition positionSouth = new BlockPosition(blockSouth.getBlockX(), blockSouth.getBlockY(), blockSouth.getBlockZ());
		BlockPosition positionNorth = new BlockPosition(blockNorth.getBlockX(), blockNorth.getBlockY(), blockNorth.getBlockZ());

		FixedAspectHolder fixedAspectHolderEast = BlocksAPI.getBlock(blockEast);
		FixedAspectHolder fixedAspectHolderWest = BlocksAPI.getBlock(blockWest);
		FixedAspectHolder fixedAspectHolderNorth = BlocksAPI.getBlock(blockNorth);
		FixedAspectHolder fixedAspectHolderSouth = BlocksAPI.getBlock(blockSouth);

		if (!(fixedAspectHolderEast == null && blockAccount.getRegenerationRegistry().getRegenerations().containsKey(positionEast)))
			blocks.add(fixedAspectHolderEast);
		else if (!(fixedAspectHolderWest == null && blockAccount.getRegenerationRegistry().getRegenerations().containsKey(positionWest)))
			blocks.add(fixedAspectHolderWest);
		else if (!(fixedAspectHolderNorth == null && blockAccount.getRegenerationRegistry().getRegenerations().containsKey(positionNorth)))
			blocks.add(fixedAspectHolderNorth);
		else if (!(fixedAspectHolderSouth == null && blockAccount.getRegenerationRegistry().getRegenerations().containsKey(positionSouth)))
			blocks.add(fixedAspectHolderSouth);

		return blocks;
	}
}
