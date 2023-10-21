package me.whereareiam.socialismus.feature.bubblechat.requirement.validator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.feature.bubblechat.requirement.BubbleRequirement;
import me.whereareiam.socialismus.feature.bubblechat.requirement.SendPermissionRequirement;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class SenderRequirementValidator {
    private final List<BubbleRequirement> requirements;

    @Inject
    public SenderRequirementValidator(SendPermissionRequirement sendPermissionRequirement) {
        this.requirements = new ArrayList<>();

        requirements.add(sendPermissionRequirement);
    }

    public boolean checkRequirements(Player sender) {
        for (BubbleRequirement requirement : requirements) {
            if (!requirement.checkRequirement(sender)) {
                return false;
            }
        }
        return true;
    }
}
