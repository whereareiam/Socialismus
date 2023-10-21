package me.whereareiam.socialismus.feature.bubblechat.requirement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.feature.bubblechat.BubbleChatConfig;
import org.bukkit.entity.Player;

@Singleton
public class SendPermissionRequirement implements BubbleRequirement {
    private final BubbleChatConfig bubbleChatConfig;

    @Inject
    public SendPermissionRequirement(BubbleChatConfig bubbleChatConfig) {
        this.bubbleChatConfig = bubbleChatConfig;
    }

    @Override
    public boolean checkRequirement(Player player) {
        return bubbleChatConfig.requirements.sendPermission == null || player.hasPermission(bubbleChatConfig.requirements.sendPermission);
    }
}
