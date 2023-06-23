package enchants.impl.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import enchants.impl.Messages;
import enchants.item.EnchantedItem;
import enchants.item.builder.SpecialItemFactory;
import enchants.records.OriginEnchant;
import me.lucko.helper.text3.Text;
import me.lucko.helper.text3.TextComponent;
import me.lucko.helper.text3.event.HoverEvent;
import me.lucko.helper.text3.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@CommandAlias("enchants")
public class EnchantCommands extends BaseCommand {

	private final JavaPlugin plugin;


	public EnchantCommands(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Subcommand("types")
	public void sendTypes(CommandSender player) {
		List<TextComponent> sendables = new ArrayList<>();
		for (OriginEnchant enchant : OriginEnchant.enchantRegistry.values()) {
			final String enchantName = enchant.name();
			final List<String> enchantInformation = enchant.information();
			enchantInformation.forEach(Text::colorize);
			Text.colorize(enchantName);

			AtomicInteger count = new AtomicInteger();
			TextComponent hoverText = TextComponent.builder(enchantName, TextColor.GREEN).hoverEvent(HoverEvent.showText(TextComponent.make(text -> {
				enchantInformation.forEach($ -> {
					count.getAndIncrement();
					if (count.get() == enchantInformation.size()) text.append(Text.colorize($));
				    else text.append(Text.colorize($) + "\n");
				});
			}))).build();

			sendables.add(hoverText);
		}

		AtomicInteger sendableCount = new AtomicInteger();
		AtomicInteger typesCount = new AtomicInteger();
		Text.sendMessage(player, TextComponent.make(text -> {
			Messages.typesHeader.forEach($ -> {
				typesCount.getAndIncrement();
				if (typesCount.get() == sendables.size()) text.append($);
				else text.append($.append(TextComponent.of('\n')));
			});

			sendables.forEach($ -> {
				sendableCount.getAndIncrement();
				if (sendableCount.get() == sendables.size()) text.append($);
				else if (sendableCount.get() == 1) text.append(TextComponent.of('\n').append($));
				else text.append($.append(TextComponent.of('\n')));
			});
		}));
	}

	@Subcommand("menu")
	public void openMenu(Player player) {
		EnchantedItem item = SpecialItemFactory.create(builder -> {
			builder.name("&b&lTesting");
			builder.lore(List.of(
					"&cTest one",
					"&aTest two"
			));
		});

		player.getInventory().addItem(item.getItemStack());
		/*
		Make sure they've got a item in their hand which can be enchanted.
		 */
	}

	@Subcommand("admin disenchant all")
	@CommandPermission("origin.admin.enchants")
	public void disenchantAll(Player player) {
		final ItemStack playerStack = player.getInventory().getItemInMainHand();

		if (!playerStack.hasItemMeta()) {
			Text.sendMessage(player, Messages.DOESNT_HAVE_ITEM_META);
			return;
		}

		final PersistentDataContainer container = playerStack.getItemMeta().getPersistentDataContainer();

		if (!OriginEnchant.canEnchant(container)) {
			Text.sendMessage(player, Messages.DOESNT_HAVE_REQUIRED_KEY);
			return;
		}

		final EnchantedItem enchantedItem = new EnchantedItem(playerStack);

		enchantedItem.removeAllEnchants();
		Text.sendMessage(player, Messages.ALL_ENCHANTS_REMOVED);
	}

	@Subcommand("admin disenchant")
	@CommandPermission("origin.admin.enchants")
	@Syntax("<enchantName> <all>")
	public void disenchantItem(Player player, String enchantName) {
		final HashMap<NamespacedKey, OriginEnchant> registry = OriginEnchant.enchantRegistry;
		final NamespacedKey key = new NamespacedKey(plugin, enchantName);
		final ItemStack playerStack = player.getInventory().getItemInMainHand();

		if (!registry.containsKey(key)) {
			Text.sendMessage(player, Messages.ENCHANT_DOESNT_EXIT);
			return;
		}

		if (!playerStack.hasItemMeta()) {
			Text.sendMessage(player, Messages.DOESNT_HAVE_ITEM_META);
			return;
		}

		final PersistentDataContainer container = playerStack.getItemMeta().getPersistentDataContainer();

		if (!OriginEnchant.canEnchant(container)) {
			Text.sendMessage(player, Messages.DOESNT_HAVE_REQUIRED_KEY);
			return;
		}

		final EnchantedItem enchantedItem = new EnchantedItem(playerStack);

		enchantedItem.removeEnchant(key);
		Text.sendMessage(player, Messages.ENCHANT_REMOVED);
	}

	@Subcommand("admin enchant")
	@CommandPermission("origin.admin.enchants")
	@Syntax("<enchantName> <levelCount>")
	public void enchantItem(Player player, String enchantName, int levelCount) {
		final HashMap<NamespacedKey, OriginEnchant> registry = OriginEnchant.enchantRegistry;
		final NamespacedKey key = new NamespacedKey(plugin, enchantName);
		final ItemStack playerStack = player.getInventory().getItemInMainHand();

		if (!registry.containsKey(key)) {
			Text.sendMessage(player, Messages.ENCHANT_DOESNT_EXIT);
			return;
		}

		final OriginEnchant enchant = registry.get(key);

		if (!playerStack.hasItemMeta()) {
			Text.sendMessage(player, Messages.DOESNT_HAVE_ITEM_META);
			return;
		}

		final PersistentDataContainer container = playerStack.getItemMeta().getPersistentDataContainer();

		if (!OriginEnchant.canEnchant(container)) {
			Text.sendMessage(player, Messages.DOESNT_HAVE_REQUIRED_KEY);
			return;
		}

		final EnchantedItem enchantedItem = new EnchantedItem(playerStack);

		enchantedItem.addEnchant(key, levelCount);
		if (enchant.maxLevel() <= levelCount) Text.sendMessage(player, Messages.ENCHANT_MAX_LEVEL);
		Text.sendMessage(player, Messages.ENCHANT_ADDED);
	}
}
