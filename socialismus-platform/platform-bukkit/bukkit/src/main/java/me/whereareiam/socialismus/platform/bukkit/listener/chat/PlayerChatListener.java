package me.whereareiam.socialismus.platform.bukkit.listener.chat;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.ComponentUtil;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.output.integration.Integration;
import me.whereareiam.socialismus.api.output.integration.SynchronizationIntegration;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import me.whereareiam.socialismus.common.chat.ChatBroadcaster;
import me.whereareiam.socialismus.common.chat.ChatCoordinator;
import me.whereareiam.socialismus.common.chat.ChatMessageFactory;
import me.whereareiam.socialismus.shared.Constants;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class PlayerChatListener implements DynamicListener<AsyncPlayerChatEvent> {
    private final ChatCoordinator chatCoordinator;
    private final ChatBroadcaster chatBroadcaster;
    private final ChatMessageFactory chatMessageFactory;
    private final Provider<Set<Integration>> integrations;

    @Inject
    public PlayerChatListener(ChatCoordinator chatCoordinator, PlayerContainerService playerContainer, BukkitAudiences audiences, ChatBroadcaster chatBroadcaster,
                              ChatMessageFactory chatMessageFactory, Provider<Set<Integration>> integrations) {
        this.chatCoordinator = chatCoordinator;
        this.chatBroadcaster = chatBroadcaster;
        this.chatMessageFactory = chatMessageFactory;
        this.integrations = integrations;
    }

    public void onEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Set<Player> recipients = event.getRecipients();
        Component content = Component.text(event.getMessage());

        FormattedChatMessage formattedChatMessage = null;
        ChatMessage chatMessage = chatMessageFactory.createChatMessage(
                player.getUniqueId(),
                recipients.stream().map(Entity::getUniqueId).collect(Collectors.toSet()),
                content
        );
        if (integrations.get().stream().anyMatch(i -> i instanceof SynchronizationIntegration))
            synchronizeChatMessage(chatMessage);
        else
            formattedChatMessage = chatCoordinator.coordinate(chatMessage);

        if (formattedChatMessage == null || formattedChatMessage.isCancelled() || !formattedChatMessage.isVanillaSending()) {
            event.setCancelled(true);
            return;
        }

        event.getRecipients().clear();
        event.getRecipients().addAll(
                formattedChatMessage.getRecipients().stream()
                        .map(recipient -> player.getServer().getPlayer(recipient.getUniqueId()))
                        .collect(Collectors.toSet())
        );

        event.setFormat(ComponentUtil.toString(
                formattedChatMessage.getFormat().replaceText(
                        chatBroadcaster.createClearReplacement(formattedChatMessage, player.getUniqueId())
                ), true
        ).replace("{message}", "%2$s"));
        event.setMessage(ComponentUtil.toString(formattedChatMessage.getContent(), true));
    }

    private void synchronizeChatMessage(ChatMessage chatMessage) {
        try {
            String data = ChatMessage.serialize(chatMessage);

            integrations.get().stream()
                    .filter(i -> i instanceof SynchronizationIntegration)
                    .findFirst()
                    .map(i -> (SynchronizationIntegration) i)
                    .ifPresent(i -> i.sync(Constants.CHANNEL, data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
