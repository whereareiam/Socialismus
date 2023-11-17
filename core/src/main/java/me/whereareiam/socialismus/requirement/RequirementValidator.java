package me.whereareiam.socialismus.requirement;

import me.whereareiam.socialismus.module.Module;
import org.bukkit.entity.Player;

import java.util.Collection;

public class RequirementValidator {
    public Collection<Player> validatePlayers(Module module, Player sender, Collection<? extends Player> players) {
        return null;
    }

    public boolean validatePlayer(Module module, Player sender) {
        return false;
    }
}
