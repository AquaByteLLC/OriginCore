package mining.impl.commands;

import blocks.block.BlockRegistry;
import blocks.block.regions.OriginRegion;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.text3.Text;
import mining.MiningAPI;
import mining.impl.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("miningregion")
public class RegionCommands extends BaseCommand {

	private final BlockRegistry registry;

	public RegionCommands(BlockRegistry registry) {
		this.registry = registry;
	}

	@Subcommand("register")
	@CommandPermission("origin.admin.regions")
	@Syntax("<regionName> <blockName>")
	public void registerRegion(Player player, String regionName, String blockName) {
		if (!registry.getBlocks().containsKey(blockName)) {
			Text.sendMessage(player, Messages.BLOCK_NOT_FOUND);
			return;
		}

		final World world = BukkitAdapter.adapt(player.getWorld());
		final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
		final RegionManager regionManager = regionContainer.get(world);

		if (!regionManager.getRegions().containsKey(regionName)) {
			Text.sendMessage(player, Messages.NO_WG_REGION);
			return;
		}

		OriginRegion region = new OriginRegion(regionName, blockName, player.getWorld());
		region.newInstance();
		Text.sendMessage(player, Messages.ORIGIN_REGION_ADDED);
	}

	@Subcommand("test")
	public void test(Player player) {
		ItemStack stack = ItemStackBuilder.of(Material.STONE_AXE).name("&c&lTesting").build();
		MiningAPI.getHelper().getBreakSpeed().setSpeed(stack, 100f);
		player.getInventory().addItem(stack);
	}
}
