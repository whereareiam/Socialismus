package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.config.ConfigManager;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.util.FormatterUtil;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

@CommandAlias("%command.main")
public class ReloadCommand extends CommandBase {
    private final ConfigManager configManager;
    private final CommandsConfig commands;
    private final MessagesConfig messages;

    @Inject
    public ReloadCommand(ConfigManager configManager, CommandsConfig commands, MessagesConfig messages) {
        this.configManager = configManager;
        this.commands = commands;
        this.messages = messages;
    }

    @Subcommand("%command.reload")
    @CommandPermission("%permission.reload")
    public void onCommand(CommandIssuer issuer) {
        if (issuer.getIssuer() instanceof Player) {
            Audience audience = issuer.getIssuer();
            audience.sendMessage(
                    FormatterUtil.formatMessage(messages.commands.reloadCommand.reloadedSuccessfully));
        }

        configManager.reloadConfigs();
    }

    @Override
    public boolean isEnabled() {
        return commands.reloadCommand.enabled;
    }

    @Override
    public void addTranslations() {
    }

    @Override
    public void addReplacements() {
        commandHelper.addReplacement(commands.reloadCommand.subCommand, "command.reload");
        commandHelper.addReplacement(commands.reloadCommand.permission, "permission.reload");
    }
}
