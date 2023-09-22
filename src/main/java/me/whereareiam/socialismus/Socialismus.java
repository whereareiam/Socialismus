package me.whereareiam.socialismus;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.whereareiam.socialismus.command.MainCommand;
import me.whereareiam.socialismus.command.ReloadCommand;
import me.whereareiam.socialismus.command.manager.CommandManager;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.util.InfoPrinterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public final class Socialismus extends JavaPlugin {
    public static String version;
    private Injector injector;
    private LoggerUtil loggerUtil;
    private BukkitAudiences bukkitAudiences;

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        bukkitAudiences = BukkitAudiences.create(this);
        version = getDescription().getVersion();

        injector = Guice.createInjector(new SocialismusConfig(this));
        loggerUtil = injector.getInstance(LoggerUtil.class);
        loggerUtil.setBukkitLogger(getLogger());

        SettingsConfig settings = injector.getInstance(SettingsConfig.class);
        MessagesConfig messages = injector.getInstance(MessagesConfig.class);
        CommandsConfig commands = injector.getInstance(CommandsConfig.class);
        settings.reload(getDataFolder().toPath().resolve("settings.yml"));
        messages.reload(getDataFolder().toPath().resolve("messages.yml"));
        commands.reload(getDataFolder().toPath().resolve("commands.yml"));

        injector.getInstance(CommandManager.class).registerCommand(MainCommand.class);
        injector.getInstance(CommandManager.class).registerCommand(ReloadCommand.class);

        InfoPrinterUtil.printStartMessage();
    }

    @Override
    public void onDisable() {
        bukkitAudiences.close();
    }
}