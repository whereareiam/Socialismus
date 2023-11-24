package me.whereareiam.socialismus.requirement;

import me.whereareiam.socialismus.module.Module;
import org.bukkit.entity.Player;

import java.util.Collection;

public class RequirementValidator {
    public Collection<? extends Player> validatePlayers(Module module, Player sender, Collection<? extends Player> players) {
        return players;
    }

    public boolean validatePlayer(Module module, Player sender) {
        switch (module) {
            case CHAT -> {
                return true;
            }

            case COMMAND -> {
                return true;
            }

            case BUBBLECHAT -> {
                return true;
            }

            case SWAPPER -> {
                return true;
            }

            default -> {
                return false;
            }
        }
    }
}
