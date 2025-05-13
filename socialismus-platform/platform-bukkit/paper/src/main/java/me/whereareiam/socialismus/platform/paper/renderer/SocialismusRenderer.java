package me.whereareiam.socialismus.platform.paper.renderer;

import io.papermc.paper.chat.ChatRenderer;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.common.chat.ChatBroadcaster;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SocialismusRenderer implements ChatRenderer {
    private final FormattedChatMessage formattedChatMessage;
    private final ChatBroadcaster chatBroadcaster;

    public SocialismusRenderer(FormattedChatMessage formattedChatMessage, ChatBroadcaster chatBroadcaster) {
        this.formattedChatMessage = formattedChatMessage;
        this.chatBroadcaster = chatBroadcaster;
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        return formattedChatMessage.getFormat()
                .replaceText(chatBroadcaster.createClearReplacement(formattedChatMessage, ((Player) viewer).getUniqueId()))
                .replaceText(chatBroadcaster.createMessageReplacement(formattedChatMessage.getContent()));
    }
}
