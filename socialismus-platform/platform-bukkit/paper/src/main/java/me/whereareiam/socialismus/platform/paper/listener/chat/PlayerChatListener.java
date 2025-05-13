package me.whereareiam.socialismus.platform.paper.listener.chat;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.whereareiam.socialismus.api.model.chat.ChatMessages;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.output.integration.Integration;
import me.whereareiam.socialismus.api.output.integration.SynchronizationIntegration;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import me.whereareiam.socialismus.common.chat.ChatBroadcaster;
import me.whereareiam.socialismus.common.chat.ChatCoordinator;
import me.whereareiam.socialismus.common.chat.ChatMessageFactory;
import me.whereareiam.socialismus.platform.paper.renderer.SocialismusRenderer;
import me.whereareiam.socialismus.shared.Constants;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class PlayerChatListener implements DynamicListener<AsyncChatEvent> {
    private final ChatCoordinator chatCoordinator;
    private final ChatMessageFactory chatMessageFactory;
    private final ChatBroadcaster chatBroadcaster;
    private final Provider<Set<Integration>> integrations;

    @Inject
    public PlayerChatListener(ChatCoordinator chatCoordinator, ChatMessageFactory chatMessageFactory, Provider<ChatSettings> chatSettings, Provider<ChatMessages> chatMessages,
                              ChatBroadcaster chatBroadcaster, Provider<Set<Integration>> integrations) {
        this.chatCoordinator = chatCoordinator;
        this.chatMessageFactory = chatMessageFactory;
        this.chatBroadcaster = chatBroadcaster;
        this.integrations = integrations;
    }

    public void onEvent(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Set<Audience> recipients = event.viewers();
        Component content = event.message();

        FormattedChatMessage formattedChatMessage = null;
        ChatMessage chatMessage = chatMessageFactory.createChatMessage(
                player.getUniqueId(),
                recipients.stream()
                        .filter(c -> !(c.getClass().getName().equals("com.destroystokyo.paper.console.TerminalConsoleCommandSender")))
                        .map(audience -> ((Player) audience).getUniqueId())
                        .collect(Collectors.toSet()),
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

        event.viewers().clear();
        event.viewers().addAll(
                formattedChatMessage.getRecipients().stream()
                        .map(recipient -> player.getServer().getPlayer(recipient.getUniqueId()))
                        .collect(Collectors.toSet())
        );

        event.renderer(new SocialismusRenderer(formattedChatMessage, chatBroadcaster));
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
