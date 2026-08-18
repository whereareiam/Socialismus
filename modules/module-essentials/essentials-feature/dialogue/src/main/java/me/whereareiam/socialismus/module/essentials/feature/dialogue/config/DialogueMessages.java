package me.whereareiam.socialismus.module.essentials.feature.dialogue.config;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Singleton
public class DialogueMessages {
	private Commands commands;

	@Getter
	@Setter
	@ToString
	public static class Commands {
		private Message message;
		private Reply reply;

		@Getter
		@Setter
		@ToString
		public static class Message {
			private String noRecipient;
			private String samePlayer;
			private String senderFormat;
			private String recipientFormat;
		}

		@Getter
		@Setter
		@ToString
		public static class Reply {
			private String noPreviousSender;
			private String noConversations;
			private String conversationListHeader;
			private String conversationListEntry;
		}
	}
}
