package enchants.impl.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import enchants.EnchantKey;
import enchants.EnchantRegistry;
import enchants.impl.EnchantPlugin;
import enchants.impl.Messages;
import enchants.impl.menu.EnchantMenuImpl;
import enchants.item.Enchant;
import enchants.item.EnchantFactory;
import enchants.item.EnchantedItem;
import me.lucko.helper.text3.Text;
import me.lucko.helper.text3.TextComponent;
import me.lucko.helper.text3.event.HoverEvent;
import me.lucko.helper.text3.format.TextColor;
import me.vadim.util.menu.Menu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@CommandAlias("enchants")
public class EnchantCommand extends BaseCommand {

	private final EnchantPlugin plugin;
	private final EnchantFactory factory;
	private final EnchantRegistry registry;

	public EnchantCommand(EnchantPlugin plugin, EnchantFactory factory, EnchantRegistry registry) {
		this.plugin   = plugin;
		this.factory  = factory;
		this.registry = registry;
	}

	@Subcommand("list")
	public void sendTypes(CommandSender player) {
		List<TextComponent> sendables = new ArrayList<>();
		for (Enchant enchant : registry.getAllEnchants()) {
			final String enchantName = enchant.getKey().getName();
			final List<String> enchantInformation = enchant.getInformation();

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
			Messages.typesHeader.forEach(it -> {
				typesCount.getAndIncrement();
				if (typesCount.get() == sendables.size()) text.append(it);
				else text.append(it.append(TextComponent.of('\n')));
			});

			sendables.forEach(it -> {
				sendableCount.getAndIncrement();
				if (sendableCount.get() == sendables.size()) text.append(it);
				else if (sendableCount.get() == 1) text.append(TextComponent.of('\n').append(it));
				else text.append(it.append(TextComponent.of('\n')));
			});
		}));
	}

	@Subcommand("menu")
	public void openMenu(Player player) {
		EnchantedItem item = arg2item(player);
		if(item == null) return;

		Menu menu = new EnchantMenuImpl(plugin, item).getMenu();
		menu.regen();
		menu.open(player);
	}

	@Subcommand("test")
	public void testItem(Player player) {
		EnchantedItem item = factory.newEnchantedItem(builder -> {
			builder.name("&b&lTesting");
		});
		item.removeAllEnchants();
		player.getInventory().addItem(item.getItemStack());
	}

	@Subcommand("admin disenchant all")
	@CommandPermission("origin.admin.enchants")
	public void disenchantAll(Player player) {
		final EnchantedItem enchantedItem = arg2item(player);
		if(enchantedItem == null) return;

		enchantedItem.removeAllEnchants();
		Text.sendMessage(player, Messages.ALL_ENCHANTS_REMOVED);
	}

	@Subcommand("admin disenchant")
	@CommandPermission("origin.admin.enchants")
	@Syntax("<enchantName> <all>")
	@CommandCompletion("@enchants")
	public void disenchantItem(Player player, EnchantKey key) {
		if (key == null) {
			Text.sendMessage(player, Messages.ENCHANT_DOESNT_EXIT);
			return;
		}

		final EnchantedItem enchantedItem = arg2item(player);
		if(enchantedItem == null) return;

		enchantedItem.removeEnchant(key);
		Text.sendMessage(player, Messages.ENCHANT_REMOVED);
	}

	@Subcommand("admin enchant")
	@CommandPermission("origin.admin.enchants")
	@Syntax("<enchantName> <levelCount>")
	@CommandCompletion("@enchants")
	public void enchantItem(Player player, EnchantKey key, int levelCount) {
		if (key == null) {
			Text.sendMessage(player, Messages.ENCHANT_DOESNT_EXIT);
			return;
		}

		final Enchant enchant = registry.getByKey(key);
		final EnchantedItem enchantedItem = arg2item(player);
		if(enchantedItem == null) return;

		enchantedItem.addEnchant(key, levelCount);
		if (levelCount > enchant.getMaxLevel()) Text.sendMessage(player, Messages.ENCHANT_MAX_LEVEL);
		Text.sendMessage(player, Messages.ENCHANT_ADDED);
	}

	private final EnchantedItem arg2item(Player player) {
		final EnchantedItem item = factory.wrapItemStack(player.getInventory().getItemInMainHand());

		if (!item.isEnchantable()) {
			Text.sendMessage(player, Messages.NOT_ENCHANTABLE);
			return null;
		}

		return item;
	}

}