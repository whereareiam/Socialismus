package me.whereareiam.socialismus.adapter.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import me.whereareiam.socialismus.adapter.config.adapter.SerializationServiceAdapter;
import me.whereareiam.socialismus.adapter.config.dynamic.ChatsConfig;
import me.whereareiam.socialismus.adapter.config.management.*;
import me.whereareiam.socialismus.adapter.config.provider.CommandsProvider;
import me.whereareiam.socialismus.adapter.config.provider.MessagesProvider;
import me.whereareiam.socialismus.adapter.config.provider.SettingsProvider;
import me.whereareiam.socialismus.adapter.config.provider.base.CoreJacksonModuleProvider;
import me.whereareiam.socialismus.adapter.config.provider.base.ObjectMapperProvider;
import me.whereareiam.socialismus.adapter.config.provider.chat.ChatMessagesProvider;
import me.whereareiam.socialismus.adapter.config.provider.chat.ChatSettingsProvider;
import me.whereareiam.socialismus.adapter.config.provider.chat.ChatsProvider;
import me.whereareiam.socialismus.adapter.config.resolver.FileSystemConfigurationTypeResolver;
import me.whereareiam.socialismus.adapter.config.template.CommandsTemplate;
import me.whereareiam.socialismus.adapter.config.template.MessagesTemplate;
import me.whereareiam.socialismus.adapter.config.template.SettingsTemplate;
import me.whereareiam.socialismus.adapter.config.template.chat.ChatMessagesTemplate;
import me.whereareiam.socialismus.adapter.config.template.chat.ChatSettingsTemplate;
import me.whereareiam.socialismus.adapter.config.template.chat.ChatTemplate;
import me.whereareiam.socialismus.api.input.registry.ObjectMapperRegistry;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatMessages;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.config.Commands;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.config.message.Messages;
import me.whereareiam.socialismus.api.output.DefaultConfig;
import me.whereareiam.socialismus.api.output.SerializationService;
import me.whereareiam.socialismus.api.output.config.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ConfigBinder extends AbstractModule {
	private final Path dataPath;
	private final Path modulesPath;
	private final Path chatPath;

	public ConfigBinder(Path dataPath) {
		this.dataPath = dataPath;
		this.modulesPath = dataPath.resolve("modules");
		this.chatPath = dataPath.resolve("chats");
	}

	@Override
	protected void configure() {
		bind(ConfigurationTypeResolver.class)
				.to(FileSystemConfigurationTypeResolver.class)
				.asEagerSingleton();

		// directories & paths
		bind(Path.class).toInstance(dataPath);
		bind(Path.class).annotatedWith(Names.named("dataPath")).toInstance(dataPath);
		bind(Path.class).annotatedWith(Names.named("modulesPath")).toInstance(modulesPath);
		bind(Path.class).annotatedWith(Names.named("chatPath")).toInstance(chatPath);
		createDirectories();

		// core services
		bind(SerializationService.class).to(SerializationServiceAdapter.class);
		bind(ConfigurationManager.class).to(DefaultConfigurationManager.class);
		bind(ConfigurationLoader.class).to(DefaultConfigurationLoader.class);
		bind(ConfigurationSaver.class).to(DefaultConfigurationSaver.class);
		bind(ConfigurationMerger.class).to(DefaultConfigurationMerger.class);

		// ————— Jackson integration —————

		bind(ObjectMapperRegistry.class)
				.to(DefaultObjectMapperRegistry.class)
				.asEagerSingleton();

		Multibinder<Module> moduleBinder =
				Multibinder.newSetBinder(binder(), Module.class);
		moduleBinder.addBinding()
				.toProvider(CoreJacksonModuleProvider.class)
				.asEagerSingleton();

		bind(ObjectMapper.class)
				.toProvider(ObjectMapperProvider.class)
				.asEagerSingleton();

		// ————— config templates —————

		MapBinder<Class<?>, DefaultConfig<?>> mapbinder =
				MapBinder.newMapBinder(
						binder(),
						new TypeLiteral<>() {},
						new TypeLiteral<>() {},
						Names.named("configTemplates")
				);
		addTemplates(mapbinder);

		// ————— other config providers —————

		bind(SettingsProvider.class);
		bind(Settings.class).toProvider(SettingsProvider.class);

		bind(MessagesProvider.class);
		bind(Messages.class).toProvider(MessagesProvider.class);

		bind(CommandsProvider.class);
		bind(new TypeLiteral<Map<String, CommandEntity>>() {})
				.toProvider(CommandsProvider.class);
		bind(new TypeLiteral<Registry<Map<String, CommandEntity>>>() {})
				.to(CommandsProvider.class)
				.asEagerSingleton();

		bind(ChatsProvider.class).asEagerSingleton();
		bind(new TypeLiteral<List<Chat>>() {})
				.annotatedWith(Names.named("chats"))
				.toProvider(ChatsProvider.class);

		bind(ChatSettingsProvider.class).asEagerSingleton();
		bind(ChatSettings.class).toProvider(ChatSettingsProvider.class);

		bind(ChatMessagesProvider.class).asEagerSingleton();
		bind(ChatMessages.class).toProvider(ChatMessagesProvider.class);
	}

	private void addTemplates(MapBinder<Class<?>, DefaultConfig<?>> mapbinder) {
		mapbinder.addBinding(Settings.class).to(SettingsTemplate.class);
		mapbinder.addBinding(Messages.class).to(MessagesTemplate.class);
		mapbinder.addBinding(Commands.class).to(CommandsTemplate.class);
		mapbinder.addBinding(ChatMessages.class).to(ChatMessagesTemplate.class);
		mapbinder.addBinding(ChatSettings.class).to(ChatSettingsTemplate.class);
		mapbinder.addBinding(ChatsConfig.class).to(ChatTemplate.class);
	}

	private void createDirectories() {
		try {
			Files.createDirectories(dataPath);
			Files.createDirectories(modulesPath);
			Files.createDirectories(chatPath);
		} catch (IOException e) {
			throw new RuntimeException("Failed to create directories", e);
		}
	}
}
