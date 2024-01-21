package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chatmention.mention.Mention;

@Singleton
public class ChatMentionFormatter {
	private final ChatMentionModule chatMentionModule;
	private final ChatMentionRequirementValidator requirementValidator;

	@Inject
	public ChatMentionFormatter(ChatMentionModule chatMentionModule, ChatMentionRequirementValidator requirementValidator) {
		this.chatMentionModule = chatMentionModule;
		this.requirementValidator = requirementValidator;
	}

	public Mention formatMention(Mention mention) {
		//TODO get correct format for player based on requirements
		//TODO format content with correct mention format

		return mention;
	}
}
