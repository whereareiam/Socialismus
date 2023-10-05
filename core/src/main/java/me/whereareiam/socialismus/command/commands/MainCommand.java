package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import com.google.inject.Inject;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

public class MainCommand extends CommandBase {
    private final CommandsConfig commands;
    private final MessagesConfig messages;

    @Inject
    public MainCommand(CommandsConfig commands, MessagesConfig messages) {
        this.commands = commands;
        this.messages = messages;
    }

    @CommandAlias("%command.main")
    @CommandPermission("%permission.main")
    public void onCommand(CommandIssuer issuer) {
        if (issuer.getIssuer() instanceof Player) {
            Player player = issuer.getIssuer();
            Audience audience = issuer.getIssuer();
            if (player.hasPermission(commands.mainCommand.staffPermission)) {
                // TODO: Print staff help
            } else {
                // TODO: Print player help
            }
        } else {
            // TODO: Print staff help
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void addTranslations() {
    }

    @Override
    public void addReplacements() {
        commandHelper.addReplacement(commands.mainCommand.command, "command.main");
        commandHelper.addReplacement(commands.mainCommand.playerPermission, "permission.main");
    }
}
