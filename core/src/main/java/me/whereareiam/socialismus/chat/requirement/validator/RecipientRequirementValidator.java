package me.whereareiam.socialismus.chat.requirement.validator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.model.Chat;
import me.whereareiam.socialismus.chat.requirement.ChatRequirement;
import me.whereareiam.socialismus.chat.requirement.SeePermissionRequirement;
import me.whereareiam.socialismus.chat.requirement.WorldRequirement;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class RecipientRequirementValidator {
    private final List<ChatRequirement> requirements;

    @Inject
    public RecipientRequirementValidator(SeePermissionRequirement seePermissionRequirement,
                                         WorldRequirement worldRequirement) {
        this.requirements = new ArrayList<>();

        requirements.add(seePermissionRequirement);
        requirements.add(worldRequirement);
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
