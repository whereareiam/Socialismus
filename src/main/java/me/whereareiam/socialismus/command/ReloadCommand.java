package me.whereareiam.socialismus.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import me.whereareiam.socialismus.config.MessagesConfig;
import me.whereareiam.socialismus.util.Formatter;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandAlias("%command.main")
public class ReloadCommand extends BaseCommand {
    private final Plugin plugin;
    private final Formatter formatter;
    private final MessagesConfig messagesConfig;

    @Inject
    public ReloadCommand(Plugin plugin, Formatter formatter, MessagesConfig messagesConfig) {
        this.plugin = plugin;
        this.formatter = formatter;
        this.messagesConfig = messagesConfig;
    }

    @Subcommand("%command.reload")
    @CommandPermission("%permission.reload")
    public void onCommand(CommandIssuer issuer) {
        if (issuer.getIssuer() instanceof Player) {
            Audience audience = issuer.getIssuer();
            audience.sendMessage(
                    formatter.formatMessage(messagesConfig.commands.reloadCommand.reloadedSuccessfully));
        }

        doReload();
    }

    private void doReload() {
        Bukkit.getPluginManager().disablePlugin(plugin);
        Bukkit.getPluginManager().enablePlugin(plugin);
    }
}
