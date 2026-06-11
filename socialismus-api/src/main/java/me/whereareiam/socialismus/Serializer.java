package me.whereareiam.socialismus;

import com.google.inject.Provider;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.keystone.template.MessageTemplate;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

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
    public static Component serialize(@NotNull String message, @NotNull String scope) {
        return getEngine().serialize(SerializerContent.builder()
                .scope(scope)
                .message(message)
                .build());
    }

    @NotNull
    public static Component serialize(@NotNull Actor actor, @NotNull String message) {
        return getEngine().serialize(actor, message);
    }

    @NotNull
    public static Component serialize(
            @NotNull Actor actor,
            @NotNull String message,
            @NotNull String scope
    ) {
        return getEngine().serialize(SerializerContent.builder()
                .receiver(actor)
                .scope(scope)
                .message(message)
                .build());
    }

	@NotNull
	public static Component serialize(@NotNull SerializerContent content) {
		return getEngine().serialize(content);
	}

	@NotNull
	public static String renderTemplate(@NotNull String template) {
		return getEngine().renderTemplate(template);
	}

	@NotNull
	public static String renderTemplate(@NotNull String template, @NotNull Map<String, String> placeholders) {
		return getEngine().renderTemplate(template, placeholders);
	}

	@NotNull
	public static String renderTemplate(
			@NotNull String template,
			@NotNull Consumer<SerializerContent.Builder> customizer
	) {
		return getEngine().renderTemplate(template, customizer);
	}

	@NotNull
	public static MessageTemplate template(@NotNull String template) {
		return getEngine().template(template);
	}

	@NotNull
	public static String placeholder(@NotNull String key) {
		Objects.requireNonNull(key, "key");

		Provider<SerializerEngine> provider = serializerProvider;
		if (provider == null) return "{" + key + "}";

		return provider.get().getPlaceholderFormat().format(key);
	}

	@NotNull
	public static SerializerEngine getEngine() {
		Provider<SerializerEngine> provider = serializerProvider;
		if (provider == null) throw new IllegalStateException("Serializer has not been initialized");

		return provider.get();
	}
}
