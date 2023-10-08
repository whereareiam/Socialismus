package me.whereareiam.socialismus.feature.chat.requirement.validator;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.feature.chat.Chat;
import me.whereareiam.socialismus.feature.chat.requirement.ChatRequirement;
import me.whereareiam.socialismus.feature.chat.requirement.WorldRequirement;
import me.whereareiam.socialismus.feature.chat.requirement.WritePermissionRequirement;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class SenderRequirementValidator {
    private final List<ChatRequirement> requirements;

    public SenderRequirementValidator() {
        this.requirements = new ArrayList<>();

        requirements.add(new WritePermissionRequirement());
        requirements.add(new WorldRequirement());
    }

    public boolean checkRequirements(Player sender, Chat chat) {
        for (ChatRequirement requirement : requirements) {
            if (!requirement.checkRequirement(sender, chat)) {
                return false;
            }
        }
        return true;
    }
}
