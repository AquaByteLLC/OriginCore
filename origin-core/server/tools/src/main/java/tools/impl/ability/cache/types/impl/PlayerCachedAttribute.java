package tools.impl.ability.cache.types.impl;

import org.bukkit.entity.Player;
import tools.impl.ability.cache.types.PlayerBasedCachedAttribute;
import tools.impl.attribute.ExpiringAttribute;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerCachedAttribute<T extends ExpiringAttribute> implements PlayerBasedCachedAttribute<T> {

	private final T attribute;
	private final Player player;
	private static final Map<Player, PlayerBasedCachedAttribute<? extends ExpiringAttribute>> cachedPlayers = new ConcurrentHashMap<>();

	public PlayerCachedAttribute(Player player, T attribute) {
		this.attribute = attribute;
		this.player = player;
		cachedPlayers.put(player, this);
	}

	@Override
	public T getAttribute() {
		return this.attribute;
	}

	@Override
	public Player getPlayer() {
		return this.player;
	}

	public static <T extends ExpiringAttribute> PlayerCachedAttribute<T> of(Class<T> clazz, Player player, T attr) {
		if (cachedPlayers.containsKey(player)) {
			return (PlayerCachedAttribute<T>) cachedPlayers.get(player);
		}
		return new PlayerCachedAttribute<>(player, attr);
	}
}
