package me.whereareiam.socialismus;

import com.google.inject.Provider;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.serializer.SerializerEngine;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Static helper around {@link SerializerEngine} to simplify serialization invocations.
 */
public final class Serializer {
	private static volatile Provider<SerializerEngine> serializerProvider;

	public static void initialize(@NotNull Provider<SerializerEngine> provider) {
		serializerProvider = Objects.requireNonNull(provider, "provider");
	}

	@NotNull
	public static Component serialize(@NotNull String message) {
		return getEngine().serialize(message);
	}

	@NotNull
	public static Component serialize(@NotNull Actor actor, @NotNull String message) {
		return getEngine().serialize(actor, message);
	}

	@NotNull
	public static Component serialize(@NotNull SerializerContent content) {
		return getEngine().serialize(content);
	}

	@NotNull
	public static SerializerEngine getEngine() {
		Provider<SerializerEngine> provider = serializerProvider;
		if (provider == null) throw new IllegalStateException("Serializer has not been initialized");

		return provider.get();
	}
}

