package me.whereareiam.socialismus.module.network.api.replication;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * A value as it was read from a {@link ReplicatedCache}, with the revision it was read at. Handing it back to
 * {@link ReplicatedCache#write} or {@link ReplicatedCache#remove(String, Versioned)} changes the entry only while it
 * still holds exactly what was read.
 *
 * <pre>{@code
 * Versioned<Preferences> read = cache.read(key).orElse(null);
 * boolean written = cache.write(key, read, changed(read), null).join();
 * }</pre>
 *
 * @param <T> value type
 */
@Getter
@ToString(of = "value")
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Versioned<T> {
	/**
	 * The value that was read.
	 */
	private final @NotNull T value;

	/**
	 * The revision of the entry the value was read from; only the cache that produced it interprets it.
	 */
	private final @NotNull Object revision;
}
