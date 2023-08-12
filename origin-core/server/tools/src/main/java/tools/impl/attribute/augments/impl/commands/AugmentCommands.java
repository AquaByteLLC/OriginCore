package tools.impl.attribute.augments.impl.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.BaseAttributeCommand;
import tools.impl.attribute.augments.Augment;
import tools.impl.attribute.augments.impl.ToolAugmentFactory;
import tools.impl.attribute.registry.impl.BaseAttributeRegistry;

@CommandAlias("augments")
public class AugmentCommands extends BaseCommand implements BaseAttributeCommand<Augment, ToolAugmentFactory> {

	private final BaseAttributeRegistry<Augment> registry;
	private final ToolAugmentFactory factory;

	public AugmentCommands() {
		this.registry = ToolsPlugin.getPlugin().getAugmentRegistry();
		this.factory = ToolsPlugin.getPlugin().getAugmentFactory();
	}

	@Override
	public BaseAttributeRegistry<Augment> getRegistry() {
		return this.registry;
	}

	@Override
	public ToolAugmentFactory getFactory() {
		return this.factory;
	}

	@Subcommand("types")
	public void openTypesMenu(CommandSender player) {
		/*
		Opens a menu of types
		 */
	}
}
