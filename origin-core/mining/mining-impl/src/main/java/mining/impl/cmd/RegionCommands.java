package mining.impl.cmd;

import blocks.block.BlockRegistry;
import blocks.block.regions.OriginRegion;
import blocks.block.regions.registry.RegionRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.data.account.BlockAccount;
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

@CommandAlias("mining")
public class RegionCommands extends BaseCommand {

	private final BlockRegistry registry;
	private final RegionRegistry regionRegistry;

	public RegionCommands(BlockRegistry registry, RegionRegistry regionRegistry) {
		this.registry = registry;
		this.regionRegistry = regionRegistry;
	}

	@Subcommand("region register")
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
		region.newInstance(regionRegistry);
		Text.sendMessage(player, Messages.ORIGIN_REGION_ADDED);
	}

	@Subcommand("region unregister")
	@CommandPermission("origin.admin.regions")
	@Syntax("<regionName>")
	public void registerRegion(Player player, String regionName) {
		final World world = BukkitAdapter.adapt(player.getWorld());
		final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
		final RegionManager regionManager = regionContainer.get(world);
		final BlockAccount account = BlocksPlugin.get().getInstance(BlocksPlugin.class).getAccounts().getAccount(player);

		if (!regionManager.getRegions().containsKey(regionName)) {
			Text.sendMessage(player, Messages.NO_WG_REGION);
			return;
		}

		if (!regionRegistry.getRegions().containsKey(regionName)) {
			Text.sendMessage(player, Messages.REGION_NOT_REGISTERED);
		}

		OriginRegion region = regionRegistry.getRegions().get(regionName).getRegion();
		Text.sendMessage(player, Messages.ORIGIN_REGION_REMOVED);
	}

	@Subcommand("test")
	public void test(Player player) {
		ItemStack stack = ItemStackBuilder.of(Material.STONE_AXE).name("&c&lTesting").build();
		MiningAPI.getHelper().getBreakSpeed().setSpeed(stack, 0.2f);
		player.getInventory().addItem(stack);
	}
}
