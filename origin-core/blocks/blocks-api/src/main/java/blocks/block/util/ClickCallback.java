package blocks.block.util;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface ClickCallback {

	void onClick(Player player, PlayerInteraction clickType);

}
