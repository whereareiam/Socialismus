package me.whereareiam.socialismus;

import com.google.inject.Guice;
import me.whereareiam.socialismus.command.management.CommandRegistrar;
import me.whereareiam.socialismus.config.ConfigManager;
import me.whereareiam.socialismus.database.DatabaseManager;
import me.whereareiam.socialismus.feature.FeatureLoader;
import me.whereareiam.socialismus.listener.PaperListenerRegistrar;
import me.whereareiam.socialismus.util.InfoPrinterUtil;

public final class SocialismusPaper extends SocialismusBase {

    @Override
    public void onEnable() {
        super.onEnable();

        injector = Guice.createInjector(new SocialismusPaperConfig(this));

        ConfigManager configManager = injector.getInstance(ConfigManager.class);
        configManager.setDataFolder(getDataFolder().toPath());
        configManager.reloadConfigs();

        injector.getInstance(CommandRegistrar.class).registerCommands();
        injector.getInstance(FeatureLoader.class).loadFeatures();

        injector.getInstance(InfoPrinterUtil.class).printStartMessage();

        injector.getInstance(PaperListenerRegistrar.class).registerListeners();
        injector.getInstance(DatabaseManager.class);
    }
}