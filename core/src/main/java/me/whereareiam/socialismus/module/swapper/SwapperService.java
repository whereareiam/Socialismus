package me.whereareiam.socialismus.module.swapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageProcessor;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.model.swapper.Swapper;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

@Singleton
public class SwapperService implements ChatMessageProcessor {
    private final LoggerUtil loggerUtil;
    private final SwapperManager swapperManager;
    private final FormatterUtil formatterUtil;
    private final MessageUtil messageUtil;

    private final SettingsConfig settingsConfig;
    private final MessagesConfig messagesConfig;

    private final Random random = new Random();

    @Inject
    public SwapperService(LoggerUtil loggerUtil, SwapperManager swapperManager,
                          FormatterUtil formatterUtil, MessageUtil messageUtil, SettingsConfig settingsConfig,
                          MessagesConfig messagesConfig) {
        this.loggerUtil = loggerUtil;
        this.swapperManager = swapperManager;
        this.formatterUtil = formatterUtil;
        this.messageUtil = messageUtil;

        this.settingsConfig = settingsConfig;
        this.messagesConfig = messagesConfig;

        loggerUtil.trace("Initializing class: " + this);
    }

    @Override
    public ChatMessage process(ChatMessage chatMessage) {
        return swapPlaceholders(chatMessage);
    }

    @Override
    public boolean isEnabled() {
        return settingsConfig.modules.swapper.enabled;
    }

    // TODO Allow swapping without chat module enabled
    private ChatMessage swapPlaceholders(ChatMessage chatMessage) {
        loggerUtil.debug("Swapping message: " + chatMessage.getContent());
        Player player = chatMessage.getSender();

        List<Swapper> swappers = swapperManager.getSwappers();
        for (Swapper swapper : swappers) {
            for (int i = 0; i < swapper.placeholders.size(); i++) {
                String placeholder = swapper.placeholders.get(i);
                if (!chatMessage.getContent().toString().contains(placeholder)) {
                    continue;
                }
                // TODO Implement requirements system
                if (!player.hasPermission(swapper.settings.permission)) {
                    loggerUtil.debug("Player didn't have the right permission");
                    String message = messagesConfig.swapper.noPermissionForSwapper;
                    if (message != null) {
                        messageUtil.sendMessage(player, message);
                    }
                    return chatMessage;
                }
                Component content;
                if (swapper.settings.randomContent) {
                    int randomIndex = random.nextInt(swapper.content.size());
                    content = formatterUtil.formatMessage(player, swapper.content.get(randomIndex));
                } else {
                    content = formatterUtil.formatMessage(player, swapper.content.get(0));
                }

                if (!swapper.contentHover.isEmpty()) {
                    StringBuilder hoverText = new StringBuilder();
                    for (int s = 0; s < swapper.contentHover.size(); s++) {
                        hoverText.append(swapper.contentHover.get(s));
                        if (s != swapper.contentHover.size() - 1) {
                            hoverText.append("\n");
                        }
                    }
                    content = content.hoverEvent(HoverEvent.showText(formatterUtil.formatMessage(player, hoverText.toString())));
                }

                content = messageUtil.replacePlaceholder(chatMessage.getContent(), placeholder, content);

                chatMessage.setContent(content);
            }
        }
        return chatMessage;
    }
}
