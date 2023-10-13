package me.whereareiam.socialismus.chat.requirement.validator;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.Chat;
import me.whereareiam.socialismus.chat.requirement.ChatRequirement;
import me.whereareiam.socialismus.chat.requirement.SeePermissionRequirement;
import me.whereareiam.socialismus.chat.requirement.WorldRequirement;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class RecipientRequirementValidator {
    private final List<ChatRequirement> requirements;

    public RecipientRequirementValidator() {
        this.requirements = new ArrayList<>();

        requirements.add(new SeePermissionRequirement());
        requirements.add(new WorldRequirement());
    }


    public boolean checkRequirements(Player recipient, Chat chat) {
        for (ChatRequirement requirement : requirements) {
            if (!requirement.checkRequirement(recipient, chat)) {
                return false;
            }
        }
        return true;
    }
}
