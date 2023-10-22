package me.whereareiam.socialismus.feature.bubblechat.requirement.validator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.feature.bubblechat.requirement.BubbleRequirement;
import org.bukkit.entity.Player;

import java.util.List;

@Singleton
public abstract class RequirementValidator {
    protected final List<BubbleRequirement> requirements;

    @Inject
    public RequirementValidator(List<BubbleRequirement> bubbleRequirements) {
        this.requirements = bubbleRequirements;
    }

    public boolean checkRequirements(Player player) {
        for (BubbleRequirement requirement : requirements) {
            if (!requirement.checkRequirement(player)) {
                return false;
            }
        }
        return true;
    }
}
