package me.whereareiam.socialismus.feature.chat;

import me.whereareiam.socialismus.feature.chat.message.ChatMessage;
import me.whereareiam.socialismus.util.DistanceCalculatorUtil;
import me.whereareiam.socialismus.util.FormatterUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatBroadcaster {

    public void broadcastMessage(ChatMessage chatMessage) {
        Player sender = chatMessage.sender();
        Chat chat = chatMessage.chat();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (shouldSendMessage(sender, player, chat.radius)) {
                if (player.hasPermission(chat.seePermission))
                    return;

                Component finalMessage = createFinalMessage(chatMessage, chat);
                Audience audience = (Audience) player;
                audience.sendMessage(finalMessage);
            }
        }
    }

    private boolean shouldSendMessage(Player sender, Player recipient, int radius) {
        return sender.equals(recipient) || DistanceCalculatorUtil.calculateDistance(sender, recipient) <= radius;
    }

    private Component createFinalMessage(ChatMessage chatMessage, Chat chat) {
        Component messageFormat = FormatterUtil.formatMessage(chatMessage.sender(), chat.messageFormat);
        Component hoverFormat = createHoverFormat(chat.hoverFormat, chatMessage.sender());

        TextReplacementConfig config = TextReplacementConfig.builder()
                .matchLiteral("{message}")
                .replacement(chatMessage.content())
                .build();

        Component finalMessage = messageFormat.replaceText(config);
        if (hoverFormat != null) {
            finalMessage = finalMessage.hoverEvent(HoverEvent.showText(hoverFormat));
        }

        return finalMessage;
    }

    private Component createHoverFormat(List<String> hoverFormatList, Player sender) {
        if (hoverFormatList == null || hoverFormatList.isEmpty()) {
            return null;
        }

        String hoverFormatString = String.join("\n", hoverFormatList);
        return FormatterUtil.formatMessage(sender, hoverFormatString);
    }
}
