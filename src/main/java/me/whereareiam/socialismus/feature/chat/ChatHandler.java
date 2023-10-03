package me.whereareiam.socialismus.feature.chat;

import com.google.inject.Inject;
import me.whereareiam.socialismus.feature.chat.message.ChatMessage;
import me.whereareiam.socialismus.feature.chat.requirement.ChatRequirement;
import me.whereareiam.socialismus.feature.chat.requirement.DistanceRequirement;
import me.whereareiam.socialismus.feature.chat.requirement.PermissionRequirement;
import me.whereareiam.socialismus.feature.chat.requirement.WorldRequirement;
import me.whereareiam.socialismus.util.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatHandler {
    private final LoggerUtil loggerUtil;
    private final List<ChatRequirement> requirements = new ArrayList<>();

    @Inject
    public ChatHandler(LoggerUtil loggerUtil) {
        this.loggerUtil = loggerUtil;

        requirements.add(new PermissionRequirement());
        requirements.add(new WorldRequirement());
        requirements.add(new DistanceRequirement());
    }

    public void handleChat(ChatMessage chatMessage) {
        for (ChatRequirement requirement : requirements) {
            if (!requirement.checkRequirement(chatMessage)) {
                return;
            }
        }

        loggerUtil.info("Chat: " + chatMessage.chat().id + " Message: " + chatMessage.content());
    }
}
