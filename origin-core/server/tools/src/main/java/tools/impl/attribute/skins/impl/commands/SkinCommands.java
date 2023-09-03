package tools.impl.attribute.skins.impl.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.BaseAttributeCommand;
import tools.impl.attribute.skins.Skin;
import tools.impl.attribute.skins.impl.ToolSkinFactory;
import tools.impl.registry.AttributeRegistry;
import tools.impl.tool.type.ISkinnedTool;

@CommandAlias("skins")
public class SkinCommands extends BaseCommand implements BaseAttributeCommand<Skin, ToolSkinFactory> {

	private final AttributeRegistry<Skin> registry;
	private final ToolSkinFactory factory;

	public SkinCommands() {
		this.registry = ToolsPlugin.getPlugin().getSkinRegistry();
		this.factory = ToolsPlugin.getPlugin().getSkinFactory();
	}

	@Override
	public AttributeRegistry<Skin> getRegistry() {
		return this.registry;
	}

	@Override
	public ToolSkinFactory getFactory() {
		return this.factory;
	}

	@Subcommand("give")
	public void giveSkin(@Flags("other") Player player, String type) {
		final ItemStack skin = ISkinnedTool.makeApplier(type);
		player.getInventory().addItem(skin);
	}

	@Subcommand("types")
	public void openTypesMenu(CommandSender player) {
		/*
		Opens a menu of types
		 */
	}
}
