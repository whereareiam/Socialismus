package me.whereareiam.socialismus.module.redis.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration backing the Redis resource provider.
 */
@Getter
@Setter
@ToString
public class RedisSettings {
	private @NotNull Redis redis = new Redis();

	/**
	 * Redis connection settings.
	 */
	@Getter
	@Setter
	@ToString
	public static class Redis {
		private @NotNull String host = "localhost";
		private int port;

		private @NotNull String password = "";

		private boolean ssl;
		private long timeout;
	}
}
