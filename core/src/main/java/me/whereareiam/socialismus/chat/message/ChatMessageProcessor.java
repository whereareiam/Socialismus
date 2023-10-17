package me.whereareiam.socialismus.chat.message;

public interface ChatMessageProcessor {
    ChatMessage process(ChatMessage chatMessage);

    boolean isEnabled();
}
