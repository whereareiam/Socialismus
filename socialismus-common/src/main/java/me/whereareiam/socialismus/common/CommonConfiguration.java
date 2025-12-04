package me.whereareiam.socialismus.common;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.common.chat.ChatCoordinator;
import me.whereareiam.socialismus.common.chat.ChatHistoryController;
import me.whereareiam.socialismus.common.chat.processor.ChatMessageProcessor;
import me.whereareiam.socialismus.common.chat.processor.FormattedChatMessageProcessor;
import me.whereareiam.socialismus.common.config.ConfiguraBootstrap;
import me.whereareiam.socialismus.common.config.SerializationServiceAdapter;
import me.whereareiam.socialismus.common.config.provider.CommandsProvider;
import me.whereareiam.socialismus.common.config.provider.MessagesProvider;
import me.whereareiam.socialismus.common.config.provider.SettingsProvider;
import me.whereareiam.socialismus.common.config.provider.chat.ChatMessagesProvider;
import me.whereareiam.socialismus.common.config.provider.chat.ChatSettingsProvider;
import me.whereareiam.socialismus.common.config.provider.chat.ChatsProvider;
import me.whereareiam.socialismus.common.config.resolver.FileSystemConfigurationTypeResolver;
import me.whereareiam.socialismus.common.container.ChatContainer;
import me.whereareiam.socialismus.common.container.ChatHistoryContainer;
import me.whereareiam.socialismus.common.event.EventController;
import me.whereareiam.socialismus.common.player.DefaultPlayerRegistry;
import me.whereareiam.socialismus.common.provider.IntegrationProvider;
import me.whereareiam.socialismus.common.provider.ReloadableProvider;
import me.whereareiam.socialismus.common.provider.SerializerEngineProvider;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;
import me.whereareiam.socialismus.common.requirement.RequirementRegistry;
import me.whereareiam.socialismus.common.requirement.validation.*;
import me.whereareiam.socialismus.common.sync.ChatNetworkBridge;
import me.whereareiam.socialismus.common.updater.provider.GitHubProvider;
import me.whereareiam.socialismus.common.updater.provider.ModrinthProvider;
import me.whereareiam.socialismus.common.updater.provider.SpigotMCProvider;
import me.whereareiam.socialismus.config.ConfigurationTypeResolver;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.UpdateProvider;
import me.whereareiam.socialismus.service.chat.ChatCoordinationService;
import me.whereareiam.socialismus.service.chat.ChatHistoryService;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import me.whereareiam.socialismus.service.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import me.whereareiam.socialismus.service.sync.ChatSyncBus;
import me.whereareiam.socialismus.type.module.ProviderType;
import me.whereareiam.socialismus.type.requirement.RequirementType;
import me.whereareiam.socialismus.util.EventUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class CommonConfiguration extends AbstractModule {
	private final Path dataPath;

	public CommonConfiguration(Path dataPath) {
		this.dataPath = dataPath;
	}

	@Override
	protected void configure() {
		requestInjection(this);

		// Configuration
		bind(ConfigurationTypeResolver.class)
				.to(FileSystemConfigurationTypeResolver.class)
				.asEagerSingleton();
		bind(ConfiguraBootstrap.class).asEagerSingleton();

		// Configs
		bind(SettingsProvider.class);
		bind(Settings.class).toProvider(SettingsProvider.class);

		bind(MessagesProvider.class);
		bind(Messages.class).toProvider(MessagesProvider.class);

		bind(CommandsProvider.class).asEagerSingleton();
		bind(Commands.class).toProvider(CommandsProvider.class);

		bind(ChatsProvider.class).asEagerSingleton();
		bind(new TypeLiteral<List<Chat>>() {
		})
				.annotatedWith(Names.named("chats"))
				.toProvider(ChatsProvider.class);

		bind(ChatSettingsProvider.class).asEagerSingleton();
		bind(ChatSettings.class).toProvider(ChatSettingsProvider.class);

		bind(ChatMessagesProvider.class).asEagerSingleton();
		bind(ChatMessages.class).toProvider(ChatMessagesProvider.class);

		// Services
		bind(SerializerEngine.class).toProvider(SerializerEngineProvider.class);
		bind(SerializationService.class).to(SerializationServiceAdapter.class);
		bind(EventManager.class).to(EventController.class);
		bind(PlayerRegistry.class).to(DefaultPlayerRegistry.class);
		bind(Socialismus.class).asEagerSingleton();
		bind(ChatCoordinationService.class).to(ChatCoordinator.class);
		bind(ChatContainerService.class).to(ChatContainer.class);
		bind(ChatHistoryContainerService.class).to(ChatHistoryContainer.class);
		bind(ChatHistoryService.class).to(ChatHistoryController.class);
		bind(ChatSyncBus.class).to(ChatNetworkBridge.class);

		// Updater
		bind(UpdateProvider.class).annotatedWith(Names.named(ProviderType.MODRINTH.toString()))
				.to(ModrinthProvider.class);
		bind(UpdateProvider.class).annotatedWith(Names.named(ProviderType.GITHUB.toString()))
				.to(GitHubProvider.class);
		bind(UpdateProvider.class).annotatedWith(Names.named(ProviderType.SPIGOT.toString()))
				.to(SpigotMCProvider.class);

		// Requirements
		bind(PermissionRequirementValidation.class).asEagerSingleton();
		bind(WorldRequirementValidation.class).asEagerSingleton();
		bind(ServerRequirementValidation.class).asEagerSingleton();
		bind(PlaceholderRequirementValidation.class).asEagerSingleton();
		bind(ChatRequirementValidation.class).asEagerSingleton();
		bind(new TypeLiteral<ExtendedRegistry<RequirementType, RequirementValidation>>() {
		}).to(RequirementRegistry.class);
		bind(RequirementEvaluatorService.class).to(RequirementEvaluator.class);

		// Other
		bind(new TypeLiteral<WorkerProcessor<ChatMessage>>() {
		}).to(ChatMessageProcessor.class);
		bind(new TypeLiteral<WorkerProcessor<FormattedChatMessage>>() {
		}).to(FormattedChatMessageProcessor.class);

		bind(new TypeLiteral<Registry<Integration>>() {
		}).to(IntegrationProvider.class).asEagerSingleton();
		bind(new TypeLiteral<Set<Integration>>() {
		}).toProvider(IntegrationProvider.class).asEagerSingleton();

		bind(new TypeLiteral<Registry<Reloadable>>() {
		}).to(ReloadableProvider.class).asEagerSingleton();
		bind(new TypeLiteral<Set<Reloadable>>() {
		}).annotatedWith(Names.named("reloadables")).toProvider(ReloadableProvider.class).asEagerSingleton();
	}

	@Inject
	void initializeSerializationHelper(Provider<SerializerEngine> serializerProvider) {
		Serializer.initialize(serializerProvider);
	}

	@Inject
	void initializeEventUtil(EventManager eventManager) {
		EventUtil.initialize(eventManager);
	}

	@Provides
	@Singleton
	Path provideBasePath() {
		return ensureDirectory(dataPath, "data");
	}

	@Provides
	@Singleton
	@Named("dataPath")
	Path provideNamedDataPath() {
		return ensureDirectory(dataPath, "data");
	}

	@Provides
	@Singleton
	@Named("modulesPath")
	Path provideModulesPath() {
		return ensureDirectory(dataPath.resolve("modules"), "modules");
	}

	@Provides
	@Singleton
	@Named("chatPath")
	Path provideChatPath() {
		return ensureDirectory(dataPath.resolve("chats"), "chats");
	}

	private Path ensureDirectory(Path path, String label) {
		try {
			Files.createDirectories(path);
			return path;
		} catch (IOException e) {
			throw new RuntimeException("Failed to create " + label + " directory", e);
		}
	}
}
