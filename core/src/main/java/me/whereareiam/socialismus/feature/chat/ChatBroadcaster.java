package me.whereareiam.socialismus.feature.chat;

import com.google.inject.Inject;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.feature.chat.message.ChatMessage;
import me.whereareiam.socialismus.util.DistanceCalculatorUtil;
import me.whereareiam.socialismus.util.FormatterUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class ChatBroadcaster {
    private final MessagesConfig messages;

    @Inject
    public ChatBroadcaster(MessagesConfig messages) {
        this.messages = messages;
    }

    public void broadcastMessage(ChatMessage chatMessage) {
        Player sender = chatMessage.sender();
        Chat chat = chatMessage.chat();

        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();

        if (onlinePlayers.isEmpty() || (onlinePlayers.size() == 1 && onlinePlayers.contains(sender))) {
            String noOnlinePlayers = messages.chat.noOnlinePlayers;
            if (noOnlinePlayers == null)
                return;

            sender.sendMessage(noOnlinePlayers);
            return;
        }

        boolean isPlayerNearby = false;

        for (Player player : onlinePlayers) {
            if (!sender.equals(player) && DistanceCalculatorUtil.calculateDistance(sender, player) <= chat.radius) {
                isPlayerNearby = true;
                break;
            }
        }

        if (!isPlayerNearby) {
            String noNearbyPlayers = messages.chat.noNearbyPlayers;
            if (noNearbyPlayers == null)
                return;

            sender.sendMessage(noNearbyPlayers);
            return;
        }

        for (Player player : onlinePlayers) {
            if (shouldSendMessage(sender, player, chat.radius)) {
                if (player.hasPermission(chat.seePermission))
                    continue;

                Audience audience = (Audience) player;
                Component finalMessage = createFinalMessage(chatMessage, chat);
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
