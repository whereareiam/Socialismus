package me.whereareiam.socialismus.module.essentials.feature.dialogue.config;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Singleton
public class DialogueSettings {
	private Synchronization synchronization;
	private MessageHistory messageHistory;
	private Reply reply;

	@Getter
	@Setter
	@ToString
	public static class Synchronization {
		private boolean enabled;
	}

	@Getter
	@Setter
	@ToString
	public static class MessageHistory {
		private boolean enabled;
		private String ttl;
		private int maxConversations;
		private int maxMessagesPerConversation;
	}

	@Getter
	@Setter
	@ToString
	public static class Reply {
		private boolean enabled;
		private String timeout;
		private boolean showRecentOnError;
		private boolean allowReplyToOffline;
	}
}
