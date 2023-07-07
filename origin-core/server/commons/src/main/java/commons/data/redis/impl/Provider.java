package commons.data.redis.impl;

import commons.CommonsPlugin;
import me.lucko.helper.messaging.Messenger;
import me.lucko.helper.redis.Redis;
import me.lucko.helper.redis.RedisCredentials;
import me.lucko.helper.redis.RedisProvider;
import me.lucko.helper.redis.plugin.HelperRedis;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class Provider implements RedisProvider {
	private final RedisCredentials redisCreds;
	private final Redis redis;

	public Provider(CommonsPlugin plugin, YamlConfiguration configuration) {
		this.redisCreds = RedisCredentials.of(
				configuration.getString("address"),
				Integer.parseInt(configuration.getString("port")),
				configuration.getString("password"));
		this.redis = getRedis(redisCreds);
		this.redis.bindWith(plugin);
		plugin.provideService(RedisProvider.class, this);
		plugin.provideService(RedisCredentials.class, redisCreds);
		plugin.provideService(Redis.class, this.redis);
		plugin.provideService(Messenger.class, this.redis);
	}

	@NotNull
	@Override
	public Redis getRedis() {
		return redis;
	}

	@NotNull
	@Override
	public Redis getRedis(@NotNull RedisCredentials redisCredentials) {
		return new HelperRedis(redisCredentials);
	}

	@NotNull
	@Override
	public RedisCredentials getGlobalCredentials() {
		return redisCreds;
	}
}
