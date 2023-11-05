package me.whereareiam.socialismus.chat.requirement.validator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.requirement.ChatRequirement;
import me.whereareiam.socialismus.model.chat.Chat;
import org.bukkit.entity.Player;

import java.util.List;

@Singleton
public abstract class RequirementValidator {
    protected final List<ChatRequirement> requirements;

    @Inject
    public RequirementValidator(List<ChatRequirement> chatRequirements) {
        this.requirements = chatRequirements;
    }

    public boolean checkRequirements(Player player, Chat chat) {
        for (ChatRequirement requirement : requirements) {
            if (!requirement.checkRequirement(player, chat)) {
                return false;
            }
        }
        return true;
    }
}
