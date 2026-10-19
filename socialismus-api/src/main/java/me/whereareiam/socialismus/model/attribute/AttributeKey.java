package me.whereareiam.socialismus.model.attribute;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * The typed key of an attribute something carries for those who know the key, such as where a chat message came
 * from on a network. Whoever adds an attribute defines its key; Socialismus itself only carries attributes along.
 *
 * <pre>{@code
 * public static final AttributeKey<Origin> ORIGIN = AttributeKey.create("socialismusnetwork", "origin", Origin.class);
 *
 * message.setAttribute(ORIGIN, Origin.remote(node));
 * Optional<Origin> origin = message.getAttribute(ORIGIN);
 * }</pre>
 *
 * @param <T> the type of the attribute's value
 */
@EqualsAndHashCode(of = {"namespace", "key"})
public final class AttributeKey<T> {
	private static final Pattern PART = Pattern.compile("[a-z0-9_.-]+");

	private final String namespace;
	private final String key;
	private final Class<T> type;

	private AttributeKey(String namespace, String key, Class<T> type) {
		this.namespace = namespace;
		this.key = key;
		this.type = type;
	}

	/**
	 * Creates the key of an attribute.
	 *
	 * @param namespace who defines the attribute, such as a module's name in lower case
	 * @param key       the attribute's name within the namespace
	 * @param type      the type of its value
	 * @param <T>       the type of its value
	 * @return the key
	 * @throws IllegalArgumentException when the namespace or key holds other than lower case letters, digits, {@code _},
	 *                                  {@code .} and {@code -}
	 */
	public static <T> @NotNull AttributeKey<T> create(@NotNull String namespace, @NotNull String key, @NotNull Class<T> type) {
		if (!PART.matcher(namespace).matches())
			throw new IllegalArgumentException("Attribute namespace must match " + PART + ": " + namespace);
		if (!PART.matcher(key).matches())
			throw new IllegalArgumentException("Attribute key must match " + PART + ": " + key);

		return new AttributeKey<>(namespace, key, type);
	}

	/**
	 * Returns the type of the attribute's value.
	 */
	public @NotNull Class<T> getType() {
		return type;
	}

	/**
	 * Returns the full key, {@code namespace:key}.
	 */
	public @NotNull String getFullKey() {
		return namespace + ":" + key;
	}

	@Override
	public String toString() {
		return getFullKey();
	}
}
