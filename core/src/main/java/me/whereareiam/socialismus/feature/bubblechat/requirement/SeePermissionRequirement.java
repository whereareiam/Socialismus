package me.whereareiam.socialismus.feature.bubblechat.requirement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.feature.bubblechat.BubbleChatConfig;
import org.bukkit.entity.Player;

@Singleton
public class SeePermissionRequirement implements BubbleRequirement {
    private final BubbleChatConfig bubbleChatConfig;

    @Inject
    public SeePermissionRequirement(BubbleChatConfig bubbleChatConfig) {
        this.bubbleChatConfig = bubbleChatConfig;
    }

    @Override
    public boolean checkRequirement(Player player) {
        return bubbleChatConfig.requirements.seePermission == null
                || bubbleChatConfig.requirements.seePermission.isEmpty()
                || player.hasPermission(bubbleChatConfig.requirements.seePermission);
    }
}
