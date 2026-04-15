package me.whereareiam.socialismus.common.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.keystone.Serializers;
import me.whereareiam.keystone.model.SerializerOptions;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.SerializerIntegration;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.registry.base.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Guice Provider for SerializerEngine instances.
 * Creates a SerializerEngine configured with Messages prefix and default settings.
 * Returns a singleton instance.
 */
@Singleton
public class SerializerEngineProvider implements Provider<SerializerEngine>, Reloadable {
	private final Provider<Messages> messagesProvider;
	private final Provider<Settings> settingsProvider;
	private final Provider<Set<Integration>> integrationsProvider;
	private volatile SerializerEngine engine;

	@Inject
	public SerializerEngineProvider(
			@NotNull Provider<Messages> messagesProvider,
			@NotNull Provider<Settings> settingsProvider,
			@NotNull Provider<Set<Integration>> integrationsProvider,
			@NotNull Registry<Reloadable> reloadables
	) {
		this.messagesProvider = messagesProvider;
		this.settingsProvider = settingsProvider;
		this.integrationsProvider = integrationsProvider;

		reloadables.register(this);
	}

	@Override
	@NotNull
	public SerializerEngine get() {
		if (engine == null) {
			Messages messages = messagesProvider.get();
			Settings settings = settingsProvider.get();
			Settings.Serialization serialization = settings.getSerialization();

			// Get serializer adapter ID from serialization config, default to "MINIMESSAGE"
			String adapter = serialization != null && serialization.getType() != null
					? serialization.getType()
					: "MINIMESSAGE";

			// Get legacy colors setting from serialization config, default to false
			boolean enableLegacyColors = serialization != null && serialization.isEnableLegacyColors();

			SerializerOptions options = SerializerOptions.builder()
					.defaultAdapter(adapter)
					.prefixSupplier(messages::getPrefix)
					.enableLegacyColors(enableLegacyColors)
					.enablePlayerNamePlaceholder(true)
					.build();

			engine = Serializers.createEngine(options);
			registerDecorators(engine);
		}

		return engine;
	}

	private void registerDecorators(@NotNull SerializerEngine engine) {
		for (Integration integration : integrationsProvider.get())
			if (integration instanceof SerializerIntegration)
				if (integration.isAvailable()) ((SerializerIntegration) integration).registerDecorator(engine);
	}

	@Override
	public void reload() {
		engine = null;
	}
}
