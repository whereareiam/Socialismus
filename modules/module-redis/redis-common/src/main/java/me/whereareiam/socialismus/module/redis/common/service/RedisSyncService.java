package me.whereareiam.socialismus.module.redis.common.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import me.whereareiam.socialismus.service.resource.sync.SyncSubscriber;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RedisSyncService implements SyncService {
	private final JedisPool jedisPool;

	@Override
	public void publish(String channel, byte[] payload) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.publish(channel.getBytes(), payload);
		}
	}

	@Override
	public void subscribe(String channel, SyncSubscriber sub) {
		new Thread(() -> {
			try (Jedis jedis = jedisPool.getResource()) {
				jedis.subscribe(new JedisPubSub() {
					@Override
					public void onMessage(String ch, String msg) {
						sub.onMessage(ch, msg.getBytes());
					}
				}, channel);
			}
		}, channel).start();
	}
}
