package me.whereareiam.socialismus.common;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
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
import me.whereareiam.socialismus.api.input.serializer.SerializationService;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.model.serializer.SerializerContent;
import me.whereareiam.socialismus.api.output.integration.Integration;
import me.whereareiam.socialismus.api.type.requirement.RequirementType;
import me.whereareiam.socialismus.api.util.EventUtil;
import me.whereareiam.socialismus.common.chat.ChatCoordinator;
import me.whereareiam.socialismus.common.chat.ChatHistoryController;
import me.whereareiam.socialismus.common.chat.processor.ChatMessageProcessor;
import me.whereareiam.socialismus.common.chat.processor.FormattedChatMessageProcessor;
import me.whereareiam.socialismus.common.container.ChatContainer;
import me.whereareiam.socialismus.common.container.ChatHistoryContainer;
import me.whereareiam.socialismus.common.container.PlayerContainer;
import me.whereareiam.socialismus.common.event.EventController;
import me.whereareiam.socialismus.common.provider.IntegrationProvider;
import me.whereareiam.socialismus.common.provider.ReloadableProvider;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;
import me.whereareiam.socialismus.common.requirement.RequirementRegistry;
import me.whereareiam.socialismus.common.requirement.validation.*;
import me.whereareiam.socialismus.common.serializer.Serializer;

import java.util.Set;

@SuppressWarnings("unused")
public class CommonConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(EventManager.class).to(EventController.class);
		bind(EventUtil.class).asEagerSingleton();

		bind(ChatCoordinationService.class).to(ChatCoordinator.class);
		bind(ChatContainerService.class).to(ChatContainer.class);
		bind(PlayerContainerService.class).to(PlayerContainer.class);
		bind(ChatHistoryContainerService.class).to(ChatHistoryContainer.class);
		bind(ChatHistoryService.class).to(ChatHistoryController.class);

		bind(new TypeLiteral<WorkerProcessor<ChatMessage>>() {}).to(ChatMessageProcessor.class);
		bind(new TypeLiteral<WorkerProcessor<FormattedChatMessage>>() {}).to(FormattedChatMessageProcessor.class);

		bind(new TypeLiteral<Registry<Integration>>() {}).to(IntegrationProvider.class).asEagerSingleton();
		bind(new TypeLiteral<Set<Integration>>() {}).toProvider(IntegrationProvider.class).asEagerSingleton();

		bind(new TypeLiteral<Registry<Reloadable>>() {}).to(ReloadableProvider.class).asEagerSingleton();
		bind(new TypeLiteral<Set<Reloadable>>() {}).annotatedWith(Names.named("reloadables")).toProvider(ReloadableProvider.class).asEagerSingleton();

		bind(SerializationService.class).to(Serializer.class);
		bind(new TypeLiteral<WorkerProcessor<SerializerContent>>() {}).to(Serializer.class);

		bind(PermissionRequirementValidation.class).asEagerSingleton();
		bind(WorldRequirementValidation.class).asEagerSingleton();
		bind(ServerRequirementValidation.class).asEagerSingleton();
		bind(PlaceholderRequirementValidation.class).asEagerSingleton();
		bind(ChatRequirementValidation.class).asEagerSingleton();
		bind(new TypeLiteral<ExtendedRegistry<RequirementType, RequirementValidation>>() {}).to(RequirementRegistry.class);
		bind(RequirementEvaluatorService.class).to(RequirementEvaluator.class);
	}
}
