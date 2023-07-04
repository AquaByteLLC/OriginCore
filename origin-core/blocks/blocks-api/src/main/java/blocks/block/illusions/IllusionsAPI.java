package blocks.block.illusions;

import org.bukkit.entity.Player;

/**
 * @author vadim
 */
public interface IllusionsAPI {

	IllusionFactory factory();

	/**
	 * @deprecated use {@link #globalRegistry()}
	 */
	@Deprecated
	IllusionRegistry registry();

	/**
	 * @return the registry for all online players
	 */
	IllusionRegistry globalRegistry();

	/**
	 * @return the player-specific registry for {@code player}
	 */
	IllusionRegistry localRegistry(Player player);

	IllusionBuilder newIllusionBuilder();

}
