package me.whereareiam.socialismus.common.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.input.container.PlayerContainerService;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.DummyPlayer;
import net.kyori.adventure.text.Component;

import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class ChatMessageFactory {
    private final PlayerContainerService playerContainer;

    @Inject
    public ChatMessageFactory(PlayerContainerService playerContainer) {
        this.playerContainer = playerContainer;
    }

    public ChatMessage createChatMessage(UUID sender, Set<UUID> recipients, Component component) {
        final Random random = new Random();

        DummyPlayer dummyPlayer = playerContainer.getPlayer(sender).orElse(null);
        Set<DummyPlayer> dummyRecipients = recipients.stream()
                .map(recipient -> playerContainer.getPlayer(recipient).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return ChatMessage.builder()
                .id(random.nextInt())
                .sender(dummyPlayer)
                .recipients(dummyRecipients)
                .content(component)
                .cancelled(false)
                .vanillaSending(false)
                .build();
    }
}
