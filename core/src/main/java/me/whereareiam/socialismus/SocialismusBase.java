package me.whereareiam.socialismus;

import com.google.inject.Injector;
import me.whereareiam.socialismus.command.management.CommandRegistrar;
import me.whereareiam.socialismus.config.ConfigManager;
import me.whereareiam.socialismus.module.ModuleLoader;
import me.whereareiam.socialismus.util.InfoPrinterUtil;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SocialismusBase extends JavaPlugin {
    public static String version;
    protected Injector injector;

    @Override
    public void onEnable() {
        version = getDescription().getVersion();

        ConfigManager configManager = injector.getInstance(ConfigManager.class);
        configManager.reloadConfigs();

        injector.getInstance(CommandRegistrar.class).registerCommands();
        injector.getInstance(ModuleLoader.class).loadModules();

        injector.getInstance(InfoPrinterUtil.class).printStartMessage();
    }

    @Override
    public void onDisable() {
        /*injector.getInstance(DatabaseManager.class).shutdown();*/
        injector.getInstance(Scheduler.class).shutdown();
    }
}