package me.whereareiam.socialismus.feature.chat;

import com.google.inject.Inject;
import me.whereareiam.socialismus.feature.chat.message.ChatMessage;
import me.whereareiam.socialismus.feature.chat.requirement.ChatRequirement;
import me.whereareiam.socialismus.feature.chat.requirement.PermissionRequirement;
import me.whereareiam.socialismus.feature.chat.requirement.WorldRequirement;

import java.util.ArrayList;
import java.util.List;

public class ChatHandler {
    private final ChatBroadcaster chatBroadcaster;
    private final List<ChatRequirement> requirements = new ArrayList<>();

    @Inject
    public ChatHandler(ChatBroadcaster chatBroadcaster) {
        this.chatBroadcaster = chatBroadcaster;

        requirements.add(new PermissionRequirement());
        requirements.add(new WorldRequirement());
    }

    public void handleChat(ChatMessage chatMessage) {
        for (ChatRequirement requirement : requirements) {
            if (!requirement.checkRequirement(chatMessage)) {
                return;
            }
        }

        chatBroadcaster.broadcastMessage(chatMessage);
    }
}

