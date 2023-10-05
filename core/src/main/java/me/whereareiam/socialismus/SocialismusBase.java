package me.whereareiam.socialismus;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.whereareiam.socialismus.command.management.CommandRegistrar;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.feature.FeatureLoader;
import me.whereareiam.socialismus.util.InfoPrinterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SocialismusBase extends JavaPlugin {
    public static String version;
    protected Injector injector;

    @Override
    public void onEnable() {
        version = getDescription().getVersion();

        injector = Guice.createInjector(new SocialismusConfig(this));
        injector.getInstance(LoggerUtil.class).setBukkitLogger(getLogger());

        SettingsConfig settings = injector.getInstance(SettingsConfig.class);
        MessagesConfig messages = injector.getInstance(MessagesConfig.class);
        CommandsConfig commands = injector.getInstance(CommandsConfig.class);
        settings.reload(getDataFolder().toPath().resolve("settings.yml"));
        messages.reload(getDataFolder().toPath().resolve("messages.yml"));
        commands.reload(getDataFolder().toPath().resolve("commands.yml"));

        injector.getInstance(CommandRegistrar.class).registerCommands();
        injector.getInstance(FeatureLoader.class).loadFeatures();

        InfoPrinterUtil.printStartMessage();
    }
}