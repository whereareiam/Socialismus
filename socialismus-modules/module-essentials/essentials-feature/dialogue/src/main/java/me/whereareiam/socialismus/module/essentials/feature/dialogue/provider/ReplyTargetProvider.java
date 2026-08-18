package me.whereareiam.socialismus.module.essentials.feature.dialogue.provider;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.conversation.ConversationService;
import org.incendo.cloud.annotations.suggestion.Suggestions;

import java.util.Collection;
import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class ReplyTargetProvider {
	private final ConversationService conversationService;

	@Suggestions("replyTargets")
	public Collection<String> suggestReplyTargets(String playerName) {
		List<String> conversations = conversationService.getPlayerConversations(playerName);

		// Return recent conversation partners
		return conversations.stream()
				.limit(10) // Limit to 10 recent conversations
				.toList();
	}

	@Suggestions("conversationPartners")
	public Collection<String> suggestConversationPartners(String playerName) {
		return suggestReplyTargets(playerName);
	}
}
