package me.whereareiam.socialismus.feature.chat.requirement;

import me.whereareiam.socialismus.feature.chat.message.ChatMessage;

public interface ChatRequirement {
    boolean checkRequirement(ChatMessage chatMessage);
}