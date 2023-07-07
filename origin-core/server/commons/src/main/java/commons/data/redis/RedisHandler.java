package commons.data.redis;

import commons.Commons;
import commons.CommonsPlugin;
import me.lucko.helper.messaging.Channel;
import me.lucko.helper.messaging.ChannelAgent;
import me.lucko.helper.messaging.ChannelListener;
import me.lucko.helper.messaging.Messenger;

import java.util.Set;

public class RedisHandler {
	private final Messenger messenger;

	public RedisHandler() {
		this.messenger = Commons.commons().getService(Messenger.class);
	}

	public <T extends RedisData> ChannelAgent<T> getAgent(Class<T> channelClazz, String channel) {
		return getChannel(channelClazz, channel).newAgent();
	}

	public <T extends RedisData> Channel<T> getChannel(Class<T> channelClazz, String channel) {
		return messenger.getChannel(channel, channelClazz);
	}

	public <T extends RedisData> void send(Class<T> channelClazz, String channel, T profile) {
		getChannel(channelClazz, channel).sendMessage(profile);
	}

	public <T extends RedisData> Set<ChannelListener<T>> getChannelListeners(Class<T> channelClazz, String channel) {
		return getAgent(channelClazz, channel).getListeners();
	}

	// SIMPLY USED TO ENSURE THE PLAYER ONLY USES CLASSES THAT ARE STATED TO BE REDIS DATA
	public interface RedisData { }
}
