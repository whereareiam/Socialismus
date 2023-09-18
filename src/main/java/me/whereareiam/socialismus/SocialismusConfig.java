package me.whereareiam.socialismus;

import com.google.inject.AbstractModule;
import me.whereareiam.socialismus.config.SettingsConfig;
import me.whereareiam.socialismus.util.Logger;

public class SocialismusConfig extends AbstractModule {
    @Override
    protected void configure() {
        bind(SettingsConfig.class).asEagerSingleton();
        bind(Logger.class).asEagerSingleton();
    }
}