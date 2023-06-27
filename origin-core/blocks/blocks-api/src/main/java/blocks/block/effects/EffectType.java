package blocks.block.effects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface EffectType<T> {
	T getType();
	EffectType<T> setType(T type);
	void handleEffect(Player player, Location location, boolean global);
}
