package me.whereareiam.socialismus.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.input.chat.ChatCoordinationService;
import me.whereareiam.socialismus.api.input.chat.ChatHistoryService;
import me.whereareiam.socialismus.api.input.container.ChatContainerService;
import me.whereareiam.socialismus.api.input.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.input.event.EventManager;
import me.whereareiam.socialismus.api.input.registry.ExtendedRegistry;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.input.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.api.input.requirement.RequirementValidation;
import me.whereareiam.socialismus.api.input.serializer.ComponentService;
import me.whereareiam.socialismus.api.input.sync.ChatSyncBus;
import me.whereareiam.socialismus.api.input.updater.UpdateProvider;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatMessages;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.config.message.Messages;
import me.whereareiam.socialismus.api.model.serializer.SerializerContent;
import me.whereareiam.socialismus.api.output.SerializationService;
import me.whereareiam.socialismus.api.output.config.ConfigurationTypeResolver;
import me.whereareiam.socialismus.api.output.integration.Integration;
import me.whereareiam.socialismus.api.type.module.ProviderType;
import me.whereareiam.socialismus.api.type.requirement.RequirementType;
import me.whereareiam.socialismus.api.util.EventUtil;
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
import me.whereareiam.socialismus.common.container.PlayerContainer;
import me.whereareiam.socialismus.common.event.EventController;
import me.whereareiam.socialismus.common.provider.IntegrationProvider;
import me.whereareiam.socialismus.common.provider.ReloadableProvider;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;
import me.whereareiam.socialismus.common.requirement.RequirementRegistry;
import me.whereareiam.socialismus.common.requirement.validation.*;
import me.whereareiam.socialismus.common.serializer.ComponentSerializer;
import me.whereareiam.socialismus.common.sync.ChatNetworkBridge;
import me.whereareiam.socialismus.common.updater.provider.GitHubProvider;
import me.whereareiam.socialismus.common.updater.provider.ModrinthProvider;
import me.whereareiam.socialismus.common.updater.provider.SpigotMCProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class CommonConfiguration extends AbstractModule {
	private final Path dataPath;

	public CommonConfiguration(Path dataPath) {
		this.dataPath = dataPath;
	}

	@Override
	protected void configure() {
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

		bind(CommandsProvider.class);
		bind(new TypeLiteral<Map<String, CommandEntity>>() {
		})
				.toProvider(CommandsProvider.class);
		bind(new TypeLiteral<Registry<Map<String, CommandEntity>>>() {
		})
				.to(CommandsProvider.class)
				.asEagerSingleton();

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
		bind(SerializationService.class).to(SerializationServiceAdapter.class);
		bind(EventManager.class).to(EventController.class);
		bind(EventUtil.class).asEagerSingleton();
		bind(ChatCoordinationService.class).to(ChatCoordinator.class);
		bind(ChatContainerService.class).to(ChatContainer.class);
		bind(PlayerContainerService.class).to(PlayerContainer.class);
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

		bind(ComponentService.class).to(ComponentSerializer.class);
		bind(new TypeLiteral<WorkerProcessor<SerializerContent>>() {
		}).to(ComponentSerializer.class);
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
