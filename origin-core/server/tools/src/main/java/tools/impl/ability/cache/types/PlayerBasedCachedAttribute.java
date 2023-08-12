package tools.impl.ability.cache.types;

import org.bukkit.entity.Player;
import tools.impl.ability.cache.CachedAttribute;
import tools.impl.attribute.ExpiringAttribute;

public interface PlayerBasedCachedAttribute<T extends ExpiringAttribute> extends CachedAttribute<T> {
	Player getPlayer();
}
