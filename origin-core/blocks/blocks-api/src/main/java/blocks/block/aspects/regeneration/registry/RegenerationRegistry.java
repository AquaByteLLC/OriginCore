package blocks.block.aspects.regeneration.registry;

import blocks.BlocksAPI;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.illusions.IllusionsAPI;
import net.minecraft.core.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface RegenerationRegistry {

	IllusionsAPI illusionsAPI = BlocksAPI.getInstance().getIllusions();

	void createRegen(Regenable block, Block original, Player player, long end);

	void deleteRegen(Regenable block);

	@NotNull HashMap<BlockPosition, Regenable> getRegenerations();

	static void cancelRegenerations(Player player, HashMap<BlockPosition, Regenable> regens) {
		regens.forEach(((location, regenable) -> {
			illusionsAPI.localRegistry(player).unregister(regenable.getFakeBlock());
			regens.remove(location);
		}));
	}

}
