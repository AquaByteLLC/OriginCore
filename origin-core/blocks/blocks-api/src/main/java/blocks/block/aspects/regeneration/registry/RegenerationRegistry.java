package blocks.block.aspects.regeneration.registry;

import blocks.block.aspects.regeneration.Regenable;
import net.minecraft.core.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface RegenerationRegistry {

	void createRegen(Regenable block, Block original, Player player, long end);

	void deleteRegen(Regenable block);

	@NotNull HashMap<BlockPosition, Regenable> getRegenerations();


	//don't do this
	//this is bad
	static void cancelRegenerations(HashMap<BlockPosition, Regenable> regens) {
		regens.forEach(((location, regenable) -> {
//			regenable.getFakeBlock().getRegistry().unregister(regenable.getFakeBlock());
			regens.remove(location);
		}));
	}

}
