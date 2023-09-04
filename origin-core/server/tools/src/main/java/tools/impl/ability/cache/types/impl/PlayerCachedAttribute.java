package tools.impl.ability.cache.types.impl;

import com.mojang.datafixers.util.Pair;
import org.bukkit.entity.Player;
import tools.impl.ability.cache.types.PlayerBasedCachedAttribute;
import tools.impl.attribute.BaseAttribute;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerCachedAttribute<T extends BaseAttribute> implements PlayerBasedCachedAttribute<T> {

	private final T attribute;
	private final Player player;
	public static final Map<Pair<UUID, ?>, PlayerBasedCachedAttribute<? extends BaseAttribute>> cachedPlayers = new ConcurrentHashMap<>();

	public PlayerCachedAttribute(Player player, T attribute) {
		this.attribute = attribute;
		this.player = player;
		cachedPlayers.put(Pair.of(player.getUniqueId(), attribute), this);
	}

	@Override
	public T getAttribute() {
		return this.attribute;
	}

	@Override
	public Player getPlayer() {
		return this.player;
	}

	@SuppressWarnings("all")
	public static <T extends BaseAttribute> PlayerCachedAttribute<T> of(Class<T> clazz, Player player, T attr) {
		if (cachedPlayers.containsKey(Pair.of(player.getUniqueId(), attr))) {
			return (PlayerCachedAttribute<T>) cachedPlayers.get(Pair.of(player.getUniqueId(), attr));
		}
		return new PlayerCachedAttribute<>(player, attr);
	}
}
