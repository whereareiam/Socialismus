package me.whereareiam.socialismus.feature.swapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageProcessor;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.feature.swapper.model.Swapper;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

@Singleton
public class SwapperService implements ChatMessageProcessor {
    private final LoggerUtil loggerUtil;
    private final SwapperManager swapperManager;
    private final FormatterUtil formatterUtil;

    private final SettingsConfig settingsConfig;
    private final MessagesConfig messagesConfig;

    @Inject
    public SwapperService(LoggerUtil loggerUtil, SwapperManager swapperManager, FormatterUtil formatterUtil,
                          SettingsConfig settingsConfig, MessagesConfig messagesConfig) {
        this.loggerUtil = loggerUtil;
        this.swapperManager = swapperManager;
        this.formatterUtil = formatterUtil;

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
        return settingsConfig.features.swapper.enabled;
    }

    private ChatMessage swapPlaceholders(ChatMessage chatMessage) {
        loggerUtil.debug("Swapping message: " + chatMessage.getContent());
        Player player = chatMessage.getSender();

        List<Swapper> swappers = swapperManager.getSwappers();
        for (Swapper swapper : swappers) {
            if (!player.hasPermission(swapper.settings.permission)) {
                loggerUtil.debug("Player didn't have the right permission");
                String message = messagesConfig.swapper.noPermissionForSwapper;
                if (message != null) {
                    Audience audience = (Audience) player;
                    audience.sendMessage(formatterUtil.formatMessage(player, message));
                }
                return chatMessage;
            }
            for (int i = 0; i < swapper.placeholders.size(); i++) {
                String placeholder = swapper.placeholders.get(i);
                Component content;
                if (swapper.settings.randomContent) {
                    int randomIndex = new Random().nextInt(swapper.contents.size());
                    content = formatterUtil.formatMessage(player, swapper.contents.get(randomIndex));
                } else {
                    content = formatterUtil.formatMessage(player, swapper.contents.get(0));
                }

                if (!swapper.hoverContent.isEmpty()) {
                    StringBuilder hoverText = new StringBuilder();
                    for (int s = 0; s < swapper.hoverContent.size(); s++) {
                        hoverText.append(swapper.hoverContent.get(s));
                        if (s != swapper.hoverContent.size() - 1) {
                            hoverText.append("\n");
                        }
                    }
                    content = content.hoverEvent(HoverEvent.showText(formatterUtil.formatMessage(player, hoverText.toString())));
                }

                TextReplacementConfig config = TextReplacementConfig.builder()
                        .matchLiteral(placeholder)
                        .replacement(content)
                        .build();

                Component newContent = chatMessage.getContent().replaceText(config);

                chatMessage.setContent(newContent);
            }
        }
        return chatMessage;
    }

}
