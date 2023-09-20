package me.whereareiam.socialismus;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.whereareiam.socialismus.command.ReloadCommand;
import me.whereareiam.socialismus.command.manager.CommandManager;
import me.whereareiam.socialismus.config.CommandsConfig;
import me.whereareiam.socialismus.config.MessagesConfig;
import me.whereareiam.socialismus.config.SettingsConfig;
import me.whereareiam.socialismus.util.InfoPrinter;
import me.whereareiam.socialismus.util.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public final class Socialismus extends JavaPlugin {
    private Injector injector;
    private Logger logger;

    public static String version;

    @Override
    public void onLoad() {
        version = getDescription().getVersion();
    }

    @Override
    public void onEnable() {
        injector = Guice.createInjector(new SocialismusConfig(this));
        logger = injector.getInstance(Logger.class);
        logger.setBukkitLogger(getLogger());

        SettingsConfig settings = injector.getInstance(SettingsConfig.class);
        MessagesConfig messages = injector.getInstance(MessagesConfig.class);
        CommandsConfig commands = injector.getInstance(CommandsConfig.class);
        settings.reload(getDataFolder().toPath().resolve("settings.yml"));
        messages.reload(getDataFolder().toPath().resolve("messages.yml"));
        commands.reload(getDataFolder().toPath().resolve("commands.yml"));

        injector.getInstance(CommandManager.class).registerCommand(ReloadCommand.class);

        InfoPrinter.printStartMessage();
    }

    @Override
    public void onDisable() {}
}