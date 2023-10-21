package me.whereareiam.socialismus.feature.bubblechat.requirement.validator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.feature.bubblechat.requirement.BubbleRequirement;
import me.whereareiam.socialismus.feature.bubblechat.requirement.SeePermissionRequirement;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class RecipientRequirementValidator {
    private final List<BubbleRequirement> requirements;

    @Inject
    public RecipientRequirementValidator(SeePermissionRequirement seePermissionRequirement) {
        this.requirements = new ArrayList<>();

        requirements.add(seePermissionRequirement);
    }


    public boolean checkRequirements(Player recipient) {
        for (BubbleRequirement requirement : requirements) {
            if (!requirement.checkRequirement(recipient)) {
                return false;
            }
        }
        return true;
    }
}