package enchants.impl;

import me.lucko.helper.text3.TextComponent;
import me.lucko.helper.text3.format.TextColor;

public interface Messages {
	TextComponent NO_PERMISSION = TextComponent.builder("You are not allowed to use this command!", TextColor.RED).build();
	TextComponent ENCHANT_ADDED = TextComponent.builder("You have removed the enchant %enchant%", TextColor.GREEN).build();
	TextComponent ENCHANT_REMOVED = TextComponent.builder("You have removed the enchant $enchant%", TextColor.GREEN).build();
}
