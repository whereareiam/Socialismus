package me.whereareiam.socialismus;

import com.google.inject.Guice;
import me.whereareiam.socialismus.command.management.CommandRegistrar;
import me.whereareiam.socialismus.config.ConfigManager;
import me.whereareiam.socialismus.feature.FeatureLoader;
import me.whereareiam.socialismus.listener.SpigotListenerRegistrar;
import me.whereareiam.socialismus.util.InfoPrinterUtil;

public final class SocialismusSpigot extends SocialismusBase {

    @Override
    public void onEnable() {
        super.onEnable();
        
        injector = Guice.createInjector(new SocialismusSpigotConfig(this));

        ConfigManager configManager = injector.getInstance(ConfigManager.class);
        configManager.setDataFolder(getDataFolder().toPath());
        configManager.reloadConfigs();

        injector.getInstance(CommandRegistrar.class).registerCommands();
        injector.getInstance(FeatureLoader.class).loadFeatures();

        injector.getInstance(InfoPrinterUtil.class).printStartMessage();

        injector.getInstance(SpigotListenerRegistrar.class).registerListeners();
    }

    @Override
    public void onDisable() {

    }
}