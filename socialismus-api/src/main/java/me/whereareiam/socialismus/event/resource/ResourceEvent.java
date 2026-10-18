package me.whereareiam.socialismus.event.resource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.SynchronousEvent;
import me.whereareiam.socialismus.model.resource.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Base of the events about a resource of the {@link me.whereareiam.socialismus.registry.ResourceRegistry}. They
 * are called on the thread that changes the registry, before the change returns.
 */
@Getter
@ToString
@RequiredArgsConstructor
public abstract class ResourceEvent implements SynchronousEvent {
	/**
	 * The key the resource is registered under.
	 */
	private final @NotNull ResourceKey<?> key;

	/**
	 * The resource.
	 */
	private final @NotNull Object resource;

	/**
	 * Returns the resource when the event is about the given key, typed by it:
	 * <pre>{@code
	 * event.resource(Constants.Resources.DATABASE).ifPresent(this::useDatabase);
	 * }</pre>
	 *
	 * @param key the key of the resource the caller follows
	 * @param <T> the type of the resource
	 * @return the resource, or empty when the event is about another key
	 * @throws IllegalArgumentException when the resource is not of the key's type
	 */
	@NotNull
	public <T> Optional<T> resource(@NotNull ResourceKey<T> key) {
		if (!this.key.getFullKey().equals(key.getFullKey())) return Optional.empty();

		return Optional.of(key.cast(resource));
	}
}
