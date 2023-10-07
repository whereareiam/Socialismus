package me.whereareiam.socialismus.feature.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.feature.chat.message.ChatMessage;
import me.whereareiam.socialismus.feature.chat.requirement.ChatRequirement;
import me.whereareiam.socialismus.feature.chat.requirement.PermissionRequirement;
import me.whereareiam.socialismus.feature.chat.requirement.WorldRequirement;
import me.whereareiam.socialismus.util.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ChatHandler {
    private final LoggerUtil loggerUtil;
    private final ChatBroadcaster chatBroadcaster;
    private final List<ChatRequirement> requirements = new ArrayList<>();

    @Inject
    public ChatHandler(LoggerUtil loggerUtil, ChatBroadcaster chatBroadcaster) {
        this.loggerUtil = loggerUtil;
        this.chatBroadcaster = chatBroadcaster;

        loggerUtil.trace("Initializing class: " + this);

        requirements.add(new PermissionRequirement());
        requirements.add(new WorldRequirement());
    }

    public void handleChat(ChatMessage chatMessage) {
        for (ChatRequirement requirement : requirements) {
            if (!requirement.checkRequirement(chatMessage)) {
                loggerUtil.debug(chatMessage.sender().getName() + " didn't met requirements");
                return;
            }
        }

        chatBroadcaster.broadcastMessage(chatMessage);
    }
}

