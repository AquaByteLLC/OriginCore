package tools.impl.attribute.enchants.impl.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.lucko.helper.text3.Text;
import me.lucko.helper.text3.TextComponent;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tools.impl.ToolsPlugin;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.BaseAttributeCommand;
import tools.impl.attribute.enchants.Enchant;
import tools.impl.attribute.enchants.impl.CustomEnchantFactory;
import tools.impl.attribute.enchants.impl.types.GeneralEnchantTypes;
import tools.impl.attribute.skins.impl.types.GeneralSkinTypes;
import tools.impl.registry.impl.BaseAttributeRegistry;
import tools.impl.tool.builder.typed.impl.UniqueItemBuilder;
import tools.impl.tool.impl.AugmentedTool;
import tools.impl.tool.impl.EnchantedTool;
import tools.impl.tool.impl.SkinnedTool;
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
		ItemStack stack = UniqueItemBuilder.create(Material.DIAMOND_PICKAXE)
				.displayName("&cTesting")
				.lore("&c{enchants}", "&d{augments}", "&e{skin}", "&f{blocks}")
				.asSpecialTool(SkinnedTool.class, item -> {
					item.makeSkinnable();
					item.addSkin(GeneralSkinTypes.FLAMINGO_PICKAXE);
				}).asSpecialTool(EnchantedTool.class, item -> {
					item.makeEnchantable();
					item.addEnchant(GeneralEnchantTypes.PLAYER_SPEED_BOOST, 10);
				}).asSpecialTool(AugmentedTool.class, item -> {
					item.makeAugmentable(1);
				}).createCustomDataUpdate("gtb", "blocks", PersistentDataType.INTEGER, 0, BlockBreakEvent.class, (ctx, breakEvent) -> {
					final Player playea = breakEvent.getPlayer();
					final ItemStack playerHand = playea.getInventory().getItemInMainHand();

					if (playerHand.getItemMeta() == null) return;

					final UniqueItemBuilder temp = UniqueItemBuilder.fromStack(playerHand);
					final NamespacedKey key = new NamespacedKey("gtb", "blocks");

					if (playerHand.getItemMeta().getPersistentDataContainer().has(key)) {
						int current = temp.getData("gtb", "blocks", PersistentDataType.INTEGER);
						temp.createCustomData("gtb", "blocks", PersistentDataType.INTEGER, (current + 1));
					}

					final EnchantedTool tool = new EnchantedTool(playerHand);
					final AugmentedTool otherTool = new AugmentedTool(playerHand);
					final SkinnedTool anotherTool = new SkinnedTool(playerHand);

					UniqueItemBuilder.updateItem(playerHand, StringPlaceholder.builder()
							.set("enchants", String.join("\n", tool.getEnchants()))
							.set("augments", String.join("\n", otherTool.getAugments()))
							.set("skin", anotherTool.getSkin() == null ? "None applied" : ToolsPlugin.getPlugin().getSkinRegistry().getByKey(anotherTool.getSkin()).getAppliedLore())
							.set("blocks", String.valueOf(temp.getData("gtb", "blocks", PersistentDataType.INTEGER)))
							.build()
					);

				}).create().build();
		player.getInventory().addItem(stack);
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
		if (levelCount > enchant.getMaxLevel())
			Text.sendMessage(player, TextComponent.make(make -> make.append("max")));
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
