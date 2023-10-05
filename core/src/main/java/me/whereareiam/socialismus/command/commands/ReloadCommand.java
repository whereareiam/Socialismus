package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.util.FormatterUtil;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandAlias("%command.main")
public class ReloadCommand extends CommandBase {
    private final Plugin plugin;
    private final CommandsConfig commandsConfig;
    private final MessagesConfig messagesConfig;

    @Inject
    public ReloadCommand(Plugin plugin, CommandsConfig commandsConfig, MessagesConfig messagesConfig) {
        this.plugin = plugin;
        this.commandsConfig = commandsConfig;
        this.messagesConfig = messagesConfig;
    }

    @Subcommand("%command.reload")
    @CommandPermission("%permission.reload")
    public void onCommand(CommandIssuer issuer) {
        if (issuer.getIssuer() instanceof Player) {
            Audience audience = issuer.getIssuer();
            audience.sendMessage(
                    FormatterUtil.formatMessage(messagesConfig.commands.reloadCommand.reloadedSuccessfully));
        }

        doReload();
    }

    private void doReload() {
        Bukkit.getPluginManager().disablePlugin(plugin);
        Bukkit.getPluginManager().enablePlugin(plugin);
    }

    @Override
    public boolean isEnabled() {
        return commandsConfig.reloadCommand.enabled;
    }

    @Override
    public void addTranslations() {
    }

    @Override
    public void addReplacements() {
        commandHelper.addReplacement(commandsConfig.reloadCommand.subCommand, "command.reload");
        commandHelper.addReplacement(commandsConfig.reloadCommand.permission, "permission.reload");
    }
}
