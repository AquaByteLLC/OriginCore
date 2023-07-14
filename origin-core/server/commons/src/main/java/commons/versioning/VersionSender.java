package commons.versioning;

import commons.hologram.InterpolatedHologram;
import commons.versioning.api.*;
import me.vadim.util.conf.bukkit.wrapper.EffectGroup;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.menu.Menu;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public record VersionSender(YamlConfiguration cfg) {

	public void openMenu(Player player,
	                     MenuVersioned menuVersioned) {
		final Menu openableMenu = player.hasResourcePack() ? menuVersioned.getNonLegacy() : menuVersioned.getLegacy();
		if (openableMenu == null) throw new RuntimeException("The menu which is attempting to be opened is null!");
		openableMenu.open(player);
	}

	public void sendMessage(Player player,
	                        MessageVersioned messageVersioned,
	                        Placeholder placeholder) {
		final String sendableMessage = player.hasResourcePack() ? messageVersioned.getNonLegacy().format(placeholder) : messageVersioned.getLegacy().format(placeholder);
		if (sendableMessage.isBlank()) return;
		player.sendMessage(sendableMessage);
	}

	public String getMessage(Player player,
	                         MessageVersioned messageVersioned,
	                         Placeholder placeholder) {
		final String sendableMessage = player.hasResourcePack() ? messageVersioned.getNonLegacy().format(placeholder) : messageVersioned.getLegacy().format(placeholder);
		if (sendableMessage.isBlank()) throw new RuntimeException("The message you are attempting the send is blank!");
		return sendableMessage;
	}

	public Menu getMenu(Player player,
	                    MenuVersioned menuVersioned) {
		final Menu openableMenu = player.hasResourcePack() ? menuVersioned.getNonLegacy() : menuVersioned.getLegacy();
		if (openableMenu == null) throw new RuntimeException("The menu which is attempting to be opened is null!");
		return openableMenu;
	}

	public InterpolatedHologram getHologram(Player player,
	                                        HologramVersioned hologramVersioned) {
		final InterpolatedHologram sendableHologram = player.hasResourcePack() ? hologramVersioned.getNonLegacy() : hologramVersioned.getLegacy();
		if (sendableHologram == null) throw new RuntimeException("The hologram you are attempting the send is null!");
		return sendableHologram;
	}

	public EffectGroup getGroup(Player player,
	                            EffectGroupVersioned effectGroupVersioned) {
		final EffectGroup sendableGroup = player.hasResourcePack() ? effectGroupVersioned.getNonLegacy() : effectGroupVersioned.getLegacy();
		if (sendableGroup == null) throw new RuntimeException("The EffectGroup you are attempting the send is null!");
		return sendableGroup;
	}
}
