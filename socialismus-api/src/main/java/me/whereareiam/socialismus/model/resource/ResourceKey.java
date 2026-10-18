package me.whereareiam.socialismus.model.resource;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Type-safe key of a shared resource, such as the synchronization, the cache or the database. A resource is
 * an object one module provides and others use; the key names it and says which type it has, so that any
 * module can define a resource of its own without changing Socialismus.
 *
 * <p>Socialismus defines the keys of its own resources in {@code Constants.Resources}. A module defines its
 * keys once, as constants, under its own namespace:
 * <pre>{@code
 * public static final ResourceKey<TranslationService> TRANSLATION =
 *         ResourceKey.create("translator", "translation", TranslationService.class);
 * }</pre>
 *
 * <p>Two keys name the same resource when their {@linkplain #getFullKey() full keys} are equal; a
 * {@code module.json} requires a resource by its full key, such as {@code socialismus:sync}.
 *
 * @param <T> the type of the resource
 */
@Getter
@EqualsAndHashCode
public final class ResourceKey<T> {
	private static final Pattern PART = Pattern.compile("[a-z0-9_.-]+");

	private final String namespace;
	private final String key;
	private final Class<T> type;

	private ResourceKey(@NotNull String namespace, @NotNull String key, @NotNull Class<T> type) {
		this.namespace = namespace;
		this.key = key;
		this.type = type;
	}

	/**
	 * Creates a resource key.
	 *
	 * @param namespace the namespace, typically the lowercase name of the module that defines the resource
	 * @param key       the name of the resource within the namespace
	 * @param type      the type every resource registered under the key has
	 * @param <T>       the type of the resource
	 * @return a new resource key
	 * @throws IllegalArgumentException when the namespace or key is not made of lowercase letters, digits,
	 *                                  {@code _}, {@code .} and {@code -}
	 */
	@NotNull
	public static <T> ResourceKey<T> create(@NotNull String namespace, @NotNull String key, @NotNull Class<T> type) {
		if (!PART.matcher(namespace).matches())
			throw new IllegalArgumentException("Resource namespace must match " + PART + ": " + namespace);
		if (!PART.matcher(key).matches())
			throw new IllegalArgumentException("Resource key must match " + PART + ": " + key);

		return new ResourceKey<>(namespace, key, type);
	}

	/**
	 * Gets the full key a {@code module.json} names the resource by.
	 *
	 * @return the full key in format "namespace:key"
	 */
	@NotNull
	public String getFullKey() {
		return namespace + ":" + key;
	}

	/**
	 * Casts a resource registered under this key's {@linkplain #getFullKey() full key} to its type.
	 *
	 * @param resource the resource
	 * @return the resource as this key's type
	 * @throws IllegalArgumentException when the resource is not of this key's type, because the module that
	 *                                  registered it defines the resource with another type
	 */
	@NotNull
	public T cast(@NotNull Object resource) {
		if (!type.isInstance(resource))
			throw new IllegalArgumentException("Resource " + getFullKey() + " is a " + resource.getClass().getName()
					+ ", not a " + type.getName());

		return type.cast(resource);
	}

	@Override
	public String toString() {
		return getFullKey();
	}
}
