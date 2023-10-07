package me.whereareiam.socialismus;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.whereareiam.socialismus.command.management.CommandRegistrar;
import me.whereareiam.socialismus.config.ConfigManager;
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

        ConfigManager configManager = injector.getInstance(ConfigManager.class);
        configManager.setDataFolder(getDataFolder().toPath());
        configManager.reloadConfigs();

        injector.getInstance(CommandRegistrar.class).registerCommands();
        injector.getInstance(FeatureLoader.class).loadFeatures();

        injector.getInstance(InfoPrinterUtil.class).printStartMessage();
    }
}