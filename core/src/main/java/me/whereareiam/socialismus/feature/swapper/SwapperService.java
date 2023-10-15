package me.whereareiam.socialismus.feature.swapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.feature.swapper.model.Swapper;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

@Singleton
public class SwapperService {
    private final LoggerUtil loggerUtil;
    private final SwapperManager swapperManager;
    private final FormatterUtil formatterUtil;
    private final MessagesConfig messagesConfig;

    @Inject
    public SwapperService(LoggerUtil loggerUtil, SwapperManager swapperManager, FormatterUtil formatterUtil, MessagesConfig messagesConfig) {
        this.loggerUtil = loggerUtil;
        this.swapperManager = swapperManager;
        this.formatterUtil = formatterUtil;
        this.messagesConfig = messagesConfig;

        loggerUtil.trace("Initializing class: " + this);
    }

    public ChatMessage swapPlaceholders(ChatMessage chatMessage) {
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
                String content;
                if (swapper.settings.randomContent) {
                    int randomIndex = new Random().nextInt(swapper.contents.size());
                    content = swapper.contents.get(randomIndex);
                } else {
                    content = swapper.contents.get(0);
                }

                String newContent;
                if (swapper.settings.directSearch) {
                    if (placeholder.startsWith("$")) {
                        String regexPattern = placeholder.substring(1);
                        newContent = chatMessage.getContent().replaceAll(regexPattern, content);
                    } else {
                        newContent = chatMessage.getContent().replaceAll(Pattern.quote(placeholder), content);
                    }
                } else {
                    newContent = chatMessage.getContent().replaceAll(" " + Pattern.quote(placeholder) + " ", " " + content + " ");
                }

                chatMessage.setContent(formatterUtil.hookIntegration(player, newContent));
            }
        }
        return chatMessage;
    }
}
