package me.whereareiam.socialismus.model.resource;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * A resource a {@link me.whereareiam.socialismus.service.resource.ResourceProvider} provides, with the key it is
 * registered under. Its type follows the key, so a resource can only be provided under a key of its type:
 * <pre>{@code
 * ProvidedResource.of(NetworkResources.TRANSPORT, transport)
 * }</pre>
 *
 * @param <T> the type of the resource
 */
@Getter
@ToString
@EqualsAndHashCode
public final class ProvidedResource<T> {
	private final ResourceKey<T> key;
	private final T resource;

	private ProvidedResource(@NotNull ResourceKey<T> key, @NotNull T resource) {
		this.key = key;
		this.resource = resource;
	}

	/**
	 * Pairs a resource with its key.
	 *
	 * @param key      the key to register the resource under
	 * @param resource the resource
	 * @param <T>      the type of the resource
	 * @return the provided resource
	 */
	@NotNull
	public static <T> ProvidedResource<T> of(@NotNull ResourceKey<T> key, @NotNull T resource) {
		return new ProvidedResource<>(key, resource);
	}
}
