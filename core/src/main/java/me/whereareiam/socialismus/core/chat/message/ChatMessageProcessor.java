package me.whereareiam.socialismus.core.chat.message;

public interface ChatMessageProcessor {
	ChatMessage process(ChatMessage chatMessage);

	boolean isEnabled();
}
