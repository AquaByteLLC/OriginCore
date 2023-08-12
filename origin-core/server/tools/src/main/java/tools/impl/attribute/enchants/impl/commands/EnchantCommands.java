package tools.impl.attribute.enchants.impl.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.lucko.helper.text3.Text;
import me.lucko.helper.text3.TextComponent;
import org.bukkit.entity.Player;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.BaseAttributeCommand;
import tools.impl.attribute.enchants.Enchant;
import tools.impl.attribute.enchants.impl.CustomEnchantFactory;
import tools.impl.registry.impl.BaseAttributeRegistry;
import tools.impl.tool.type.IEnchantedTool;

@CommandAlias("enchants")
public class EnchantCommands extends BaseCommand implements BaseAttributeCommand<Enchant, CustomEnchantFactory> {

	private final BaseAttributeRegistry<Enchant> registry;
	private final CustomEnchantFactory factory;

	public EnchantCommands() {
		this.registry = ToolsPlugin.getPlugin().getEnchantRegistry();
		this.factory = ToolsPlugin.getPlugin().getEnchantFactory();
	}

	@Override
	public BaseAttributeRegistry<Enchant> getRegistry() {
		return this.registry;
	}

	@Override
	public CustomEnchantFactory getFactory() {
		return this.factory;
	}

	@Subcommand("menu")
	public void openMenu(Player player) {
		IEnchantedTool item = arg2item(player);
		if (item == null) return;

		/*
		Menu menu = new EnchantMenu(plugin, item).getMenu();
		menu.regen();
		menu.open(player);

		 */
	}

	@Subcommand("test")
	public void testItem(Player player) {
		IEnchantedTool item = factory.newAttributeItem(builder -> {
			builder.name("&b&lTesting");
		});
		item.removeAllEnchants();
		player.getInventory().addItem(item.getItemStack());
	}

	@Subcommand("admin disenchant all")
	@CommandPermission("origin.admin.enchants")
	public void disenchantAll(Player player) {
		final IEnchantedTool enchantedItem = arg2item(player);
		if (enchantedItem == null) return;

		enchantedItem.removeAllEnchants();
		// Text.sendMessage(player, Messages.ALL_ENCHANTS_REMOVED);
	}

	@Subcommand("admin disenchant")
	@CommandPermission("origin.admin.enchants")
	@Syntax("<enchantName> <all>")
	@CommandCompletion("@enchants")
	public void disenchantItem(Player player, AttributeKey key) {
		if (key == null) {
			// Text.sendMessage(player, Messages.ENCHANT_DOESNT_EXIT);
			return;
		}

		final IEnchantedTool enchantedItem = arg2item(player);
		if (enchantedItem == null) return;

		enchantedItem.removeEnchant(key);
		// Text.sendMessage(player, Messages.ENCHANT_REMOVED);
	}

	@Subcommand("admin enchant")
	@CommandPermission("origin.admin.enchants")
	@Syntax("<enchantName> <levelCount>")
	@CommandCompletion("@enchants")
	public void enchantItem(Player player, AttributeKey key, int levelCount) {
		if (key == null) {
			// Text.sendMessage(player, Messages.ENCHANT_DOESNT_EXIT);
			return;
		}

		final Enchant enchant = registry.getByKey(key);
		final IEnchantedTool enchantedItem = arg2item(player);
		if (enchantedItem == null) return;

		enchantedItem.addEnchant(key, levelCount);
		if (levelCount > enchant.getMaxLevel()) Text.sendMessage(player, TextComponent.make(make -> make.append("max")));
		// Text.sendMessage(player, Messages.ENCHANT_ADDED);
	}

	private final IEnchantedTool arg2item(Player player) {
		final IEnchantedTool item = factory.wrapItemStack(player.getInventory().getItemInMainHand());

		if (!item.isEnchantable()) {
			// Text.sendMessage(player, Messages.NOT_ENCHANTABLE);
			return null;
		}

		return item;
	}

}
