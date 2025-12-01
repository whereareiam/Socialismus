package me.whereareiam.socialismus.common.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import net.kyori.adventure.text.Component;

import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class ChatMessageFactory {
    private final PlayerRegistry playerRegistry;

    @Inject
    public ChatMessageFactory(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    public ChatMessage createChatMessage(UUID sender, Set<UUID> recipients, Component component) {
        final Random random = new Random();

	    SocialismusPlayer player = playerRegistry.getPlayerData(sender).orElse(null);
        Set<SocialismusPlayer> socialismusRecipients = recipients.stream()
                .map(recipient -> playerRegistry.getPlayerData(recipient).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return ChatMessage.builder()
                .id(random.nextInt())
                .sender(player)
                .recipients(socialismusRecipients)
                .content(component)
                .cancelled(false)
                .vanillaSending(false)
                .build();
    }
}
