package mining.impl;

import me.lucko.helper.text3.TextComponent;
import me.lucko.helper.text3.format.TextColor;

public interface Messages {
	TextComponent NO_WG_REGION = TextComponent.builder("There is no WorldGuard region with this name!", TextColor.RED).build();
	TextComponent ORIGIN_REGION_ADDED = TextComponent.builder("The region has been set.", TextColor.GREEN).build();
	TextComponent BLOCK_NOT_FOUND = TextComponent.builder("The block you specified wasn't found!", TextColor.RED).build();
}
