package me.whereareiam.socialismus.module.essentials.feature.dialogue;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import me.whereareiam.socialismus.module.essentials.api.model.feature.Feature;
import me.whereareiam.socialismus.module.essentials.api.feature.FeatureInitializer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueCommands;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueMessages;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueSettings;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.provider.DialogueCommandsProvider;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.provider.DialogueMessagesProvider;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.provider.DialogueSettingsProvider;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.provider.ReplyTargetProvider;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.ReplyValidator;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.conversation.ConversationRenderer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.conversation.ConversationService;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.DialogueErrorHandler;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.type.LocalDialogueService;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.type.SyncDialogueService;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.message.MessageDeliveryCoordinator;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.message.MessageFactory;

public class DialogueConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		// Bind config classes to their providers
		bind(DialogueSettings.class).toProvider(DialogueSettingsProvider.class);
		bind(DialogueMessages.class).toProvider(DialogueMessagesProvider.class);
		bind(DialogueCommands.class).toProvider(DialogueCommandsProvider.class);

		bind(LocalDialogueService.class).asEagerSingleton();
		bind(SyncDialogueService.class).asEagerSingleton();
		bind(ConversationService.class).asEagerSingleton();
		bind(MessageFactory.class).asEagerSingleton();
		bind(ReplyValidator.class).asEagerSingleton();
		bind(DialogueErrorHandler.class).asEagerSingleton();
		bind(ConversationRenderer.class).asEagerSingleton();
		bind(MessageDeliveryCoordinator.class).asEagerSingleton();
		bind(ReplyTargetProvider.class).asEagerSingleton();

		bind(new TypeLiteral<FeatureInitializer<? extends Feature>>() {}).to(Dialogue.class);
	}
}
