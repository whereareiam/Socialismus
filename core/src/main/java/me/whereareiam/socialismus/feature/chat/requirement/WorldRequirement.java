package me.whereareiam.socialismus.feature.chat.requirement;

import me.whereareiam.socialismus.feature.chat.message.ChatMessage;

public class WorldRequirement implements ChatRequirement {
    @Override
    public boolean checkRequirement(ChatMessage chatMessage) {
        return chatMessage.chat().worlds == null
                || chatMessage.chat().worlds.isEmpty()
                || chatMessage.chat().worlds.contains(chatMessage.sender().getWorld().getName());
    }
}