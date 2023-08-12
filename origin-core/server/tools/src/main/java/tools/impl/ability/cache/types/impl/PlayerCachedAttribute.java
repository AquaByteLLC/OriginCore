package tools.impl.ability.cache.types.impl;

import org.bukkit.entity.Player;
import tools.impl.ability.cache.types.PlayerBasedCachedAttribute;
import tools.impl.attribute.ExpiringAttribute;

public class PlayerCachedAttribute<T extends ExpiringAttribute> implements PlayerBasedCachedAttribute<T> {
	private final T attribute;
	private final Player player;

	public PlayerCachedAttribute(Player player, T attribute) {
		this.attribute = attribute;
		this.player = player;
	}

	@Override
	public T getAttribute() {
		return this.attribute;
	}

	@Override
	public Player getPlayer() {
		return this.player;
	}
}
