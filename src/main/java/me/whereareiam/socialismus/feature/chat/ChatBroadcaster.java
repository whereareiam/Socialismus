package me.whereareiam.socialismus.feature.chat;

import me.whereareiam.socialismus.feature.chat.message.ChatMessage;
import me.whereareiam.socialismus.util.DistanceCalculatorUtil;
import me.whereareiam.socialismus.util.FormatterUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatBroadcaster {

    public void broadcastMessage(ChatMessage chatMessage) {
        Player sender = chatMessage.sender();
        Chat chat = chatMessage.chat();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (sender.equals(player) || DistanceCalculatorUtil.calculateDistance(sender, player) <= chat.radius) {
                Component messageFormat = FormatterUtil.formatMessage(chatMessage.sender(), chat.messageFormat);

                TextReplacementConfig config = TextReplacementConfig.builder()
                        .matchLiteral("{message}")
                        .replacement(chatMessage.content())
                        .build();

                Component finalMessage = messageFormat.replaceText(config);

                Audience audience = (Audience) chatMessage.sender();
                audience.sendMessage(finalMessage);
            }
        }
    }
}
