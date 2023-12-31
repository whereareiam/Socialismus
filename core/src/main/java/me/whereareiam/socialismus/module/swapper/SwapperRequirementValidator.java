package me.whereareiam.socialismus.module.swapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.model.swapper.Swapper;
import me.whereareiam.socialismus.util.MessageUtil;
import org.bukkit.entity.Player;

@Singleton
public class SwapperRequirementValidator {
    private final MessageUtil messageUtil;
    private final MessagesConfig messagesConfig;

    @Inject
    public SwapperRequirementValidator(MessageUtil messageUtil, MessagesConfig messagesConfig) {
        this.messageUtil = messageUtil;
        this.messagesConfig = messagesConfig;
    }

    public boolean validatePlayer(Swapper swapper, Player player, boolean shouldSendMessage) {
        if (!swapper.requirements.enabled)
            return true;

        if (!player.hasPermission(swapper.requirements.usePermission)) {
            if (shouldSendMessage)
                messageUtil.sendMessage(player, messagesConfig.swapper.noUsePermission);

            return false;
        }

        return true;
    }
}
