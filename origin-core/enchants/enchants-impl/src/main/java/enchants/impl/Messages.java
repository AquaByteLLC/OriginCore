package enchants.impl;

import me.lucko.helper.text3.Text;
import me.lucko.helper.text3.TextComponent;
import me.lucko.helper.text3.format.TextColor;

import java.util.List;

public interface Messages {
	TextComponent NO_PERMISSION = TextComponent.builder("You are not allowed to use this command!", TextColor.RED).build();
	TextComponent ENCHANT_ADDED = TextComponent.builder("You have added the enchant!", TextColor.GREEN).build();
	TextComponent ENCHANT_REMOVED = TextComponent.builder("You have removed the enchant!", TextColor.GREEN).build();
	TextComponent ENCHANT_MAX_LEVEL   = TextComponent.builder("The enchant level you specified was too high, the item has been given the max level of this enchant!", TextColor.GREEN).build();
	TextComponent NOT_ENCHANTABLE     = TextComponent.builder("This item isn't enchantable!", TextColor.RED).build();
	TextComponent ENCHANT_DOESNT_EXIT = TextComponent.builder("The enchant doesn't exist!", TextColor.RED).build();
	TextComponent ALL_ENCHANTS_REMOVED = TextComponent.builder("All of the enchants on this item have been removed!", TextColor.GREEN).build();
	List<TextComponent> helpMessage = List.of();
	List<TextComponent> typesHeader = List.of(
			TextComponent.builder(Text.colorize("&b&lEnchant Types")).build(),
			TextComponent.builder("").build()
	);
}
