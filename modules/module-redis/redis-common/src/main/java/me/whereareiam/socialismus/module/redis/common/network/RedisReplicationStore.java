package me.whereareiam.socialismus.module.redis.common.network;

import me.whereareiam.socialismus.module.network.api.transport.ReplicationStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Keeps the network's replicated caches in Redis: each entry is a key of its own, {@code socialismus:network:} followed
 * by the cache's namespace, a separator and the entry's key, with Redis's own expiry. Conditional writes are Lua
 * scripts, so the comparison and the change are one step for every node.
 */
public final class RedisReplicationStore implements ReplicationStore {
	private static final String PREFIX = "socialismus:network:";
	private static final String SEPARATOR = "\u001F";

	private static final String COMPARE_AND_PUT = """
			local current = redis.call('GET', KEYS[1])
			if ARGV[1] == '0' then
				if current then return 0 end
			elseif current ~= ARGV[2] then
				return 0
			end
			if ARGV[4] == '-1' then
				redis.call('SET', KEYS[1], ARGV[3])
			else
				redis.call('SET', KEYS[1], ARGV[3], 'PX', ARGV[4])
			end
			return 1
			""";

	private static final String COMPARE_AND_REMOVE = """
			if redis.call('GET', KEYS[1]) == ARGV[1] then
				redis.call('DEL', KEYS[1])
				return 1
			end
			return 0
			""";

	private final JedisPool pool;

	public RedisReplicationStore(@NotNull JedisPool pool) {
		this.pool = pool;
	}

	@Override
	public @NotNull Optional<byte[]> get(@NotNull String namespace, @NotNull String key) {
		try (Jedis jedis = pool.getResource()) {
			return Optional.ofNullable(jedis.get(id(namespace, key)));
		}
	}

	@Override
	public @NotNull Map<String, byte[]> entries(@NotNull String namespace) {
		String prefix = PREFIX + namespace + SEPARATOR;
		ScanParams params = new ScanParams().match(escape(prefix) + "*").count(500);
		Map<String, byte[]> entries = new LinkedHashMap<>();

		try (Jedis jedis = pool.getResource()) {
			String cursor = ScanParams.SCAN_POINTER_START;
			do {
				ScanResult<String> page = jedis.scan(cursor, params);
				List<String> keys = page.getResult();
				if (!keys.isEmpty()) {
					List<byte[]> values = jedis.mget(keys.stream().map(k -> k.getBytes(StandardCharsets.UTF_8)).toArray(byte[][]::new));
					for (int i = 0; i < keys.size(); i++) {
						if (values.get(i) != null) entries.put(keys.get(i).substring(prefix.length()), values.get(i));
					}
				}
				cursor = page.getCursor();
			} while (!ScanParams.SCAN_POINTER_START.equals(cursor));
		}

		return entries;
	}

	@Override
	public boolean compareAndPut(
			@NotNull String namespace,
			@NotNull String key,
			byte @Nullable [] expected,
			byte @NotNull [] value,
			@Nullable Duration ttl
	) {
		List<byte[]> args = new ArrayList<>(4);
		args.add(bytes(expected == null ? "0" : "1"));
		args.add(expected == null ? new byte[0] : expected);
		args.add(value);
		args.add(bytes(ttl == null ? "-1" : Long.toString(Math.max(1, ttl.toMillis()))));

		try (Jedis jedis = pool.getResource()) {
			return Long.valueOf(1).equals(jedis.eval(bytes(COMPARE_AND_PUT), List.of(id(namespace, key)), args));
		}
	}

	@Override
	public boolean compareAndRemove(@NotNull String namespace, @NotNull String key, byte @NotNull [] expected) {
		try (Jedis jedis = pool.getResource()) {
			return Long.valueOf(1).equals(jedis.eval(bytes(COMPARE_AND_REMOVE), List.of(id(namespace, key)), List.of(expected)));
		}
	}

	private static byte[] id(String namespace, String key) {
		return bytes(PREFIX + namespace + SEPARATOR + key);
	}

	private static String escape(String pattern) {
		return pattern.replaceAll("([*?\\[\\]\\\\])", "\\\\$1");
	}

	private static byte[] bytes(String text) {
		return text.getBytes(StandardCharsets.UTF_8);
	}
}
