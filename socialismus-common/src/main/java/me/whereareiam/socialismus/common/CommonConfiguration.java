package me.whereareiam.socialismus.common;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.whereareiam.conditia.adapter.configura.ConditiaFeature;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.feature.polymorphic.PolymorphicFeature;
import me.whereareiam.configura.feature.postprocess.PostProcessFeature;
import me.whereareiam.configura.type.Format;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.common.chat.ChatCoordinator;
import me.whereareiam.socialismus.common.chat.ChatHistoryController;
import me.whereareiam.socialismus.common.chat.ChatLifecycle;
import me.whereareiam.socialismus.common.chat.DefaultChatOwnershipService;
import me.whereareiam.socialismus.common.chat.processor.ChatMessageProcessor;
import me.whereareiam.socialismus.common.chat.processor.FormattedChatMessageProcessor;
import me.whereareiam.socialismus.common.chat.render.ClearPlaceholderResolver;
import me.whereareiam.socialismus.common.chat.render.DefaultChatRenderService;
import me.whereareiam.socialismus.common.chat.requirement.DefaultChatRequirementService;
import me.whereareiam.socialismus.common.chat.requirement.RequirementLifecycle;
import me.whereareiam.socialismus.common.chat.toggle.DefaultChatToggleService;
import me.whereareiam.socialismus.common.chat.toggle.InMemoryChatToggleStore;
import me.whereareiam.socialismus.common.config.ConfigurationLifecycle;
import me.whereareiam.socialismus.common.config.SerializationServiceAdapter;
import me.whereareiam.socialismus.common.config.SocialismusConfiguraModule;
import me.whereareiam.socialismus.common.config.migration.SocialismusConfigMigrations;
import me.whereareiam.socialismus.common.config.provider.CommandsProvider;
import me.whereareiam.socialismus.common.config.provider.MessagesProvider;
import me.whereareiam.socialismus.common.config.provider.SettingsProvider;
import me.whereareiam.socialismus.common.config.provider.UpdaterProvider;
import me.whereareiam.socialismus.common.config.provider.chat.ChatMessagesProvider;
import me.whereareiam.socialismus.common.config.provider.chat.ChatSettingsProvider;
import me.whereareiam.socialismus.common.config.provider.chat.ChatsProvider;
import me.whereareiam.socialismus.common.config.resolver.FileSystemConfigurationTypeResolver;
import me.whereareiam.socialismus.common.container.ChatContainer;
import me.whereareiam.socialismus.common.container.ChatHistoryContainer;
import me.whereareiam.socialismus.common.event.EventController;
import me.whereareiam.socialismus.common.integration.IntegrationLifecycle;
import me.whereareiam.socialismus.common.logging.LoggingLifecycle;
import me.whereareiam.socialismus.common.player.DefaultPlayerRegistry;
import me.whereareiam.socialismus.common.provider.*;
import me.whereareiam.socialismus.common.requirement.RequirementConfiguration;
import me.whereareiam.socialismus.common.updater.UpdateScheduler;
import me.whereareiam.socialismus.common.updater.provider.GitHubProvider;
import me.whereareiam.socialismus.common.updater.provider.ModrinthProvider;
import me.whereareiam.socialismus.common.updater.provider.SpigotMCProvider;
import me.whereareiam.socialismus.config.ConfigurationTypeResolver;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.model.config.Updater;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.UpdateProvider;
import me.whereareiam.socialismus.service.chat.ChatCoordinationService;
import me.whereareiam.socialismus.service.chat.ChatHistoryService;
import me.whereareiam.socialismus.service.chat.ChatOwnershipService;
import me.whereareiam.socialismus.service.chat.ChatRequirementService;
import me.whereareiam.socialismus.service.chat.ChatToggleService;
import me.whereareiam.socialismus.service.chat.ChatToggleStore;
import me.whereareiam.socialismus.service.chat.render.ChatMessageTransformer;
import me.whereareiam.socialismus.service.chat.render.ChatPlaceholderResolver;
import me.whereareiam.socialismus.service.chat.render.ChatRenderService;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import me.whereareiam.socialismus.service.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.type.ConfigurationType;
import me.whereareiam.socialismus.type.module.ProviderType;
import me.whereareiam.socialismus.util.EventUtil;
import me.whereareiam.strata.Strata;
import me.whereareiam.strata.adapter.configura.StrataFeature;

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

		// Lifecycles, started phase by phase and within a phase in this order
		Multibinder<RuntimeLifecycle> lifecycles = Multibinder.newSetBinder(binder(), RuntimeLifecycle.class);
		lifecycles.addBinding().to(ConfigurationLifecycle.class);
		lifecycles.addBinding().to(LoggingLifecycle.class);
		lifecycles.addBinding().to(IntegrationLifecycle.class);
		lifecycles.addBinding().to(ChatLifecycle.class);
		lifecycles.addBinding().to(RequirementLifecycle.class);
		lifecycles.addBinding().to(ConnectionLifecycle.class);
		lifecycles.addBinding().to(UpdateScheduler.class);

		// Configuration
		bind(ConfigurationTypeResolver.class)
				.to(FileSystemConfigurationTypeResolver.class)
				.asEagerSingleton();

		// Configs
		bind(SettingsProvider.class);
		bind(Settings.class).toProvider(SettingsProvider.class);

		bind(UpdaterProvider.class);
		bind(Updater.class).toProvider(UpdaterProvider.class);

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

		bind(ChatToggleStore.class).to(InMemoryChatToggleStore.class);
		bind(ChatToggleService.class).to(DefaultChatToggleService.class);

		// Services
		bind(SerializerEngine.class).toProvider(SerializerEngineProvider.class);
		bind(SerializationService.class).to(SerializationServiceAdapter.class);
		bind(EventManager.class).to(EventController.class);
		bind(PlayerRegistry.class).to(DefaultPlayerRegistry.class);
		bind(ChatCoordinationService.class).to(ChatCoordinator.class);
		bind(ChatContainerService.class).to(ChatContainer.class);
		bind(ChatHistoryContainerService.class).to(ChatHistoryContainer.class);
		bind(ChatHistoryService.class).to(ChatHistoryController.class);
		bind(ChatOwnershipService.class).to(DefaultChatOwnershipService.class);
		bind(ChatRenderService.class).to(DefaultChatRenderService.class);

		// Updater
		bind(UpdateProvider.class).annotatedWith(Names.named(ProviderType.MODRINTH.toString()))
				.to(ModrinthProvider.class);
		bind(UpdateProvider.class).annotatedWith(Names.named(ProviderType.GITHUB.toString()))
				.to(GitHubProvider.class);
		bind(UpdateProvider.class).annotatedWith(Names.named(ProviderType.SPIGOT.toString()))
				.to(SpigotMCProvider.class);

		// Requirements
		install(new RequirementConfiguration());
		bind(ChatRequirementService.class).to(DefaultChatRequirementService.class);

		// Other
		bind(new TypeLiteral<WorkerProcessor<ChatMessage>>() {
		}).to(ChatMessageProcessor.class);
		bind(new TypeLiteral<WorkerProcessor<FormattedChatMessage>>() {
		}).to(FormattedChatMessageProcessor.class);

		bind(new TypeLiteral<Registry<ChatMessageTransformer>>() {
		}).to(ChatMessageTransformerProvider.class).asEagerSingleton();
		bind(new TypeLiteral<Set<ChatMessageTransformer>>() {
		}).toProvider(ChatMessageTransformerProvider.class).asEagerSingleton();

		bind(new TypeLiteral<Registry<ChatPlaceholderResolver>>() {
		}).to(ChatPlaceholderResolverProvider.class).asEagerSingleton();
		bind(new TypeLiteral<Set<ChatPlaceholderResolver>>() {
		}).toProvider(ChatPlaceholderResolverProvider.class).asEagerSingleton();

		bind(ClearPlaceholderResolver.class).asEagerSingleton();

		Multibinder.newSetBinder(binder(), Integration.class, Names.named("integrationCandidates"));

		bind(new TypeLiteral<Registry<Integration>>() {
		}).to(IntegrationProvider.class).asEagerSingleton();
		bind(new TypeLiteral<Set<Integration>>() {
		}).toProvider(IntegrationProvider.class).asEagerSingleton();

		bind(new TypeLiteral<Registry<Reloadable>>() {
		}).to(ReloadableProvider.class).asEagerSingleton();
		bind(new TypeLiteral<Set<Reloadable>>() {
		}).annotatedWith(Names.named("reloadables")).toProvider(ReloadableProvider.class);
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
	Strata provideStrata(Configura configura, @Named("dataPath") Path dataPath) {
		return new Strata(List.of(SocialismusConfigMigrations.on(configura, dataPath)));
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
		return ensureDirectory(dataPath.resolve("chat"), "chat");
	}

	@Provides
	@Singleton
	Configura provideConfigura(@Named("dataPath") Path dataPath) {
		ConfigurationType type = new FileSystemConfigurationTypeResolver(dataPath).getConfigurationType();
		Format format = type == ConfigurationType.JSON ? Format.JSON : Format.YAML;

		return Config.builder()
				.format(format)
				.module(new SocialismusConfiguraModule())
				.feature(PostProcessFeature.defaults())
				.feature(PolymorphicFeature.defaults())
				.feature(new ConditiaFeature())
				.feature(new StrataFeature())
				.build();
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
