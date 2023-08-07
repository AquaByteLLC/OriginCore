package commons.versioning;

import commons.hologram.InterpolatedHologram;
import commons.versioning.api.EffectGroupVersioned;
import commons.versioning.api.HologramVersioned;
import commons.versioning.api.MenuVersioned;
import commons.versioning.api.MessageVersioned;
import me.vadim.util.conf.bukkit.wrapper.EffectGroup;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.menu.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public record VersionSender(File file, YamlConfiguration cfg) {

	public void openMenu(Player player, MenuVersioned menuVersioned) {
		final Menu openableMenu = player.hasResourcePack() ? menuVersioned.nonLegacy() : menuVersioned.legacy();
		if (openableMenu == null) throw new RuntimeException("The menu which is attempting to be opened is null!");
		openableMenu.open(player);
	}

	public void sendMessage(Player player, MessageVersioned messageVersioned, Placeholder placeholder) {
		final String sendableMessage = player.hasResourcePack() ? messageVersioned.nonLegacy().format(placeholder) : messageVersioned.legacy().format(placeholder);

		player.sendMessage(ChatColor.translateAlternateColorCodes('&', sendableMessage));
	}

	public String getMessage(Player player, MessageVersioned messageVersioned, Placeholder placeholder) {
		final String sendableMessage = player.hasResourcePack() ? messageVersioned.nonLegacy().format(placeholder) : messageVersioned.legacy().format(placeholder);
		if (sendableMessage.isBlank()) throw new RuntimeException("The message you are attempting the send is blank!");
		return ChatColor.translateAlternateColorCodes('&', sendableMessage);
	}

	public Menu getMenu(Player player, MenuVersioned menuVersioned) {
		final Menu openableMenu = player.hasResourcePack() ? menuVersioned.nonLegacy() : menuVersioned.legacy();
		if (openableMenu == null) throw new RuntimeException("The menu which is attempting to be opened is null!");
		return openableMenu;
	}

	public InterpolatedHologram getHologram(Player player, HologramVersioned hologramVersioned) {
		final InterpolatedHologram sendableHologram = player.hasResourcePack() ? hologramVersioned.nonLegacy() : hologramVersioned.legacy();
		if (sendableHologram == null) throw new RuntimeException("The hologram you are attempting the send is null!");
		return sendableHologram;
	}

	public EffectGroup getGroup(Player player, EffectGroupVersioned effectGroupVersioned) {
		final EffectGroup sendableGroup = player.hasResourcePack() ? effectGroupVersioned.nonLegacy() : effectGroupVersioned.legacy();
		if (sendableGroup == null) throw new RuntimeException("The EffectGroup you are attempting the send is null!");
		return sendableGroup;
	}

}
