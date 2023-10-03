package me.whereareiam.socialismus.feature.chat.requirement;

import me.whereareiam.socialismus.feature.chat.message.ChatMessage;

public class PermissionRequirement implements ChatRequirement {
    @Override
    public boolean checkRequirement(ChatMessage chatMessage) {
        return chatMessage.chat().writePermission == null || chatMessage.sender().hasPermission(chatMessage.chat().writePermission);
    }
}
