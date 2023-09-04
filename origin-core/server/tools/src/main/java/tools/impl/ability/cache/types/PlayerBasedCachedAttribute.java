package tools.impl.ability.cache.types;

import org.bukkit.entity.Player;
import tools.impl.ability.cache.CachedAttribute;
import tools.impl.attribute.BaseAttribute;

public interface PlayerBasedCachedAttribute<T extends BaseAttribute> extends CachedAttribute<T> {
	Player getPlayer();


}
