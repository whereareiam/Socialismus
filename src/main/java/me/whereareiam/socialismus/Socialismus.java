package me.whereareiam.socialismus;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

public final class Socialismus extends JavaPlugin {
    private Injector injector;

    @Override
    public void onLoad() {
        injector = Guice.createInjector(new SocialismusConfig());
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}