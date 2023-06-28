package blocks.block.aspects.overlay;

import org.bukkit.entity.Player;

/**
 * @author vadim
 */
@FunctionalInterface
public interface ClickCallback {

	enum Interaction {
		LEFT_CLICK,
		RIGHT_CLICK;
	}

	void onClick(Player player, Interaction clickType);

}
