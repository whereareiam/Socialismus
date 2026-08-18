package me.whereareiam.socialismus.module.essentials.feature.dialogue.service;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueSettings;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.conversation.ConversationService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ReplyValidator {
	private final ConversationService conversationService;
	private final Provider<DialogueSettings> settings;

	public boolean isSelfMessage(SocialismusPlayer sender, String recipient) {
		return sender.getUsername().equals(recipient);
	}

	public boolean isReplyEnabled() {
		return settings.get().getReply().isEnabled();
	}

	public boolean conversationExists(String player1, String player2) {
		return conversationService.getConversation(player1, player2).isPresent();
	}
}
