package me.whereareiam.socialismus.adapter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import me.whereareiam.socialismus.adapter.config.adapter.SerializationServiceAdapter;
import me.whereareiam.socialismus.adapter.config.dynamic.ChatsConfig;
import me.whereareiam.socialismus.adapter.config.management.ConfigLoader;
import me.whereareiam.socialismus.adapter.config.management.ConfigManager;
import me.whereareiam.socialismus.adapter.config.management.ConfigMerger;
import me.whereareiam.socialismus.adapter.config.management.ConfigSaver;
import me.whereareiam.socialismus.adapter.config.provider.CommandsProvider;
import me.whereareiam.socialismus.adapter.config.provider.MessagesProvider;
import me.whereareiam.socialismus.adapter.config.provider.SettingsProvider;
import me.whereareiam.socialismus.adapter.config.provider.chat.ChatMessagesProvider;
import me.whereareiam.socialismus.adapter.config.provider.chat.ChatSettingsProvider;
import me.whereareiam.socialismus.adapter.config.provider.chat.ChatsProvider;
import me.whereareiam.socialismus.adapter.config.template.CommandsTemplate;
import me.whereareiam.socialismus.adapter.config.template.MessagesTemplate;
import me.whereareiam.socialismus.adapter.config.template.SettingsTemplate;
import me.whereareiam.socialismus.adapter.config.template.chat.ChatMessagesTemplate;
import me.whereareiam.socialismus.adapter.config.template.chat.ChatSettingsTemplate;
import me.whereareiam.socialismus.adapter.config.template.chat.ChatTemplate;
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
import me.whereareiam.socialismus.api.output.config.ConfigurationLoader;
import me.whereareiam.socialismus.api.output.config.ConfigurationManager;
import me.whereareiam.socialismus.api.output.config.ConfigurationMerger;
import me.whereareiam.socialismus.api.output.config.ConfigurationSaver;

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
		bind(Path.class).toInstance(dataPath);
		bind(Path.class).annotatedWith(Names.named("dataPath")).toInstance(dataPath);
		bind(Path.class).annotatedWith(Names.named("modulesPath")).toInstance(modulesPath);
		bind(Path.class).annotatedWith(Names.named("chatPath")).toInstance(chatPath);
		createDirectories();

		bind(SerializationService.class).to(SerializationServiceAdapter.class);
		bind(ConfigurationManager.class).to(ConfigManager.class);
		bind(ConfigurationLoader.class).to(ConfigLoader.class);
		bind(ConfigurationSaver.class).to(ConfigSaver.class);
		bind(ConfigurationMerger.class).to(ConfigMerger.class);

		bind(ObjectMapper.class).toProvider(ConfigManager.class).asEagerSingleton();
		MapBinder<Class<?>, DefaultConfig<?>> mapbinder = MapBinder.newMapBinder(binder(), new TypeLiteral<>() {}, new TypeLiteral<>() {});
		addTemplates(mapbinder);

		bind(SettingsProvider.class);
		bind(Settings.class).toProvider(SettingsProvider.class);
		bind(MessagesProvider.class);
		bind(Messages.class).toProvider(MessagesProvider.class);
		bind(CommandsProvider.class);
		bind(new TypeLiteral<Map<String, CommandEntity>>() {}).toProvider(CommandsProvider.class);
		bind(new TypeLiteral<Registry<Map<String, CommandEntity>>>() {}).to(CommandsProvider.class).asEagerSingleton();

		bind(ChatsProvider.class).asEagerSingleton();
		bind(new TypeLiteral<List<Chat>>() {}).annotatedWith(Names.named("chats")).toProvider(ChatsProvider.class);
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