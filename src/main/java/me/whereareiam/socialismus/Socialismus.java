package me.whereareiam.socialismus;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.whereareiam.socialismus.config.SettingsConfig;
import me.whereareiam.socialismus.util.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public final class Socialismus extends JavaPlugin {
    private Injector injector;
    private Logger logger;

    @Override
    public void onLoad() {
        injector = Guice.createInjector(new SocialismusConfig());
        logger = injector.getInstance(Logger.class);
        logger.setBukkitLogger(getLogger());
    }

    @Override
    public void onEnable() {
        SettingsConfig settings = injector.getInstance(SettingsConfig.class);
        settings.reload(getDataFolder().toPath().resolve("settings.yml"));
    }

    @Override
    public void onDisable() {}
}